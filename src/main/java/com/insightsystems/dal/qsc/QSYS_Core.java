package com.insightsystems.dal.qsc;

import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.time.Instant;
import java.util.*;

/**
 * QSC Q-SYS Core Device Adapter
 * Company: Insight Systems
 * @author Jayden Loone (@JaydenL-Insight)
 * @version 0.1
 *
 * Monitored Statistics:
 * - Firmware
 * - Serial Number
 * - Device name
 * - Uptime
 * - Device running status
 * - Running Systems
 *      - System Status
 *      - Design Versions
 *      - Design Creation and Update times
 *      - Design runtime
 *      - Statuses
 *      - All Items within a system
 *          - Name
 *          - Model
 *          - Status
 *          - Type
 *          - Redundancy Info
 */
public class QSYS_Core extends RestCommunicator implements Monitorable {
    private static final String BASE_URI = "api/v0/" ;

    @Override
    protected void authenticate() throws Exception {}

    @Override
    public List<Statistics> getMultipleStatistics() throws Exception {
        ExtendedStatistics extStats = new ExtendedStatistics();
        Map<String,String> stats = new HashMap<>();

        //Self Core status
        JsonNode self = this.doGet(BASE_URI+"cores/self?meta=permissions",JsonNode.class);
        addStat(stats,self,"Device#Name","/data/name");
        addStat(stats,self,"Device#firmwareVersion","/data/firmware");
        addStat(stats,self,"Device#Hostname","/data/hostname");
        addStat(stats,self,"Device#Model","/data/model");
        addStat(stats,self,"Device#SerialNumber","/data/serial");
        addStat(stats,self,"deviceOnline","/data/engineOnline");
        addStat(stats,self,"Device#Access","/data/access");

        addUptime(stats,self,"Device#engineUpSince","/data/engineUptime");
        addUptime(stats,self,"Device#UpSince","/data/uptime");

        addStat(stats,self,"Status#code","/data/status/code");
        addStat(stats,self,"Status#State","/data/status/name");

        //Device Time
        JsonNode time = this.doGet(BASE_URI+"cores/self/config/time?meta=permissions",JsonNode.class);
        addStat(stats,time,"Device#time","/data/dateTimeUTC");
        // "/meta/permissions/canManageTime" weather this user can manage time

        JsonNode systemResponse = this.doGet(BASE_URI+"systems?meta=permissions",JsonNode.class);
        // "/meta/permissions/canManage" if device can manage systems

        ArrayNode systems = (ArrayNode) systemResponse.at("/data");
        for (int i = 0; i < systems.size();i++){
            JsonNode system = systems.get(i);
            String sysPre = "System" + system.at("/id").asText();
            String sysId = system.at("/id").asText();

            stats.put(sysPre+"#id",sysId);
            addStat(stats,system,sysPre+"#code","/code");
            addStat(stats,system,sysPre+"#id","/id");
            addStat(stats,system,sysPre+"#name","/name");
            addStat(stats,system,sysPre+"#statusCode","/status/code");
            addStat(stats,system,sysPre+"#statusName","/status/name");
            addStat(stats,system,sysPre+"#statusMessage","/status/message");
            addStat(stats,system,sysPre+"#designVersion","/revision/version");
            addStat(stats,system,sysPre+"#designLastUpdate","/revision/updated");
            addStat(stats,system,sysPre+"#designCreated","/revision/created");
            addStat(stats,system,sysPre+"#designIsEmulated","/revision/design/isEmulated");
            addStat(stats,system,sysPre+"#designIsRedundant","/revision/design/isRedundant");
            addStat(stats,system,sysPre+"#designCode","/revision/design/code");
            addUptime(stats,system,sysPre+"#designUpSince","/revision/design/uptime");

            JsonNode itemResponse = this.doGet(BASE_URI+"/systems/"+ sysId+"/items?meta=permissions",JsonNode.class);
            // "/meta/permissions/canManageItemSettings" if device can manage settings

            ArrayNode items = (ArrayNode) itemResponse.at("/data");
            for (int j = 0; j < systems.size();j++){
                JsonNode item = items.get(j);
                String itemPre = sysPre + "Item" +  (j+1) + "#";
                String deviceName = item.at("/name").asText();

                stats.put(itemPre+"name",deviceName);
                addStat(stats,item,itemPre+"model","/model");
                addStat(stats,item,itemPre+"make","/manufacturer");
                addStat(stats,item,itemPre+"type","/type");
                addStat(stats,item,itemPre+"id","/id");
                addStat(stats,item,itemPre+"statusCode","/status/code");
                addStat(stats,item,itemPre+"statusMessage","/status/message");
                addStat(stats,item,itemPre+"statusDetails","/status/details");

                if (item.at("/redundancy/isRedundant").asBoolean()){
                    stats.put(itemPre+ "redundancyEnabled","true");

                    addStat(stats,item,itemPre+"backupActive","/redundancy/backupActive");
                    addStat(stats,item,itemPre+"backupName","/redundancy/backupActive");

                    if (item.at("/redundancy/primaryName").asText().equals(deviceName)){
                        stats.put(itemPre+"redundancyRole","Primary");
                        addStat(stats,item,itemPre+"active","/redundancy/primaryActive");
                        addStat(stats,item,itemPre+"backupName","/redundancy/backupName");
                    } else {
                        stats.put(itemPre+"redundancyRole","Backup");
                        addStat(stats,item,itemPre+"active","/redundancy/backupActive");
                        addStat(stats,item,itemPre+"primaryName","/redundancy/primaryName");
                    }
                } else {
                    stats.put(itemPre+ "redundancyEnabled","false");
                }
                addUptime(stats,item,itemPre + "startedAt","/startedAt");
            }
        }

        extStats.setStatistics(stats);
        return Collections.singletonList(extStats);
    }

    private void addUptime(Map<String, String> stats, JsonNode self,String name, String jsonPath) {
        long uptime = self.at(jsonPath).asLong(0L);
        if (uptime > 0L) {
            Date date = Date.from(Instant.ofEpochMilli(uptime));
            stats.put(name,date.toString());
        }

    }

    private static void addStat(Map<String,String>stats, JsonNode json, String name, String jsonPath){
        stats.put(name, json.at(jsonPath).asText(""));
    }

    public static void main(String[] args) throws Exception {
        QSYS_Core core = new QSYS_Core();
        core.setHost("192.168.0.116");
        core.setLogin("");
        core.setPassword("");
        core.setProtocol("http");
        core.init();

        ((ExtendedStatistics)core.getMultipleStatistics().get(0)).getStatistics().forEach((k,v)-> System.out.println(k + " : " + v));
    }
}
