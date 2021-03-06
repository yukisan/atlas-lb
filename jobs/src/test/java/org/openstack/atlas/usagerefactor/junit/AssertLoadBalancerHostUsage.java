package org.openstack.atlas.usagerefactor.junit;

import org.junit.Assert;
import org.openstack.atlas.service.domain.events.UsageEvent;
import org.openstack.atlas.service.domain.usage.entities.LoadBalancerHostUsage;
import org.openstack.atlas.service.domain.usage.entities.LoadBalancerMergedHostUsage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class AssertLoadBalancerHostUsage {

        public static void hasValues(Integer accountId, Integer lbId, Integer hostId, Long incomingTransfer,
                                 Long incomingTransferSsl, Long outgoingTransfer, Long outgoingTransferSsl,
                                 Integer ccs, Integer ccsSsl, Integer numVips, Integer tags,
                                 UsageEvent eventType, String pollTime,
                                 LoadBalancerHostUsage actualUsage) throws ParseException {
            Assert.assertEquals(accountId.intValue(), actualUsage.getAccountId());
            Assert.assertEquals(lbId.intValue(), actualUsage.getLoadbalancerId());
            Assert.assertEquals(hostId.intValue(), actualUsage.getHostId());
            Assert.assertEquals(incomingTransfer.longValue(), actualUsage.getIncomingTransfer());
            Assert.assertEquals(incomingTransferSsl.longValue(), actualUsage.getIncomingTransferSsl());
            Assert.assertEquals(outgoingTransfer.longValue(), actualUsage.getOutgoingTransfer());
            Assert.assertEquals(outgoingTransferSsl.longValue(), actualUsage.getOutgoingTransferSsl());
            Assert.assertEquals(ccs.intValue(), actualUsage.getConcurrentConnections());
            Assert.assertEquals(ccsSsl.intValue(), actualUsage.getConcurrentConnectionsSsl());
            Assert.assertEquals(numVips.intValue(), actualUsage.getNumVips());
            Assert.assertEquals(tags.intValue(), actualUsage.getTagsBitmask());
            Assert.assertEquals(eventType, actualUsage.getEventType());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String actualTimeStr = sdf.format(actualUsage.getPollTime().getTime());
            Assert.assertEquals(pollTime, actualTimeStr);
    }

    public static void containsValues(Integer accountId, Integer lbId, Integer hostId, Long incomingTransfer,
                                 Long incomingTransferSsl, Long outgoingTransfer, Long outgoingTransferSsl,
                                 Integer ccs, Integer ccsSsl, Integer numVips, Integer tags,
                                 UsageEvent eventType, String pollTime,
                                      List<LoadBalancerHostUsage> actualUsageList) throws ParseException {
            boolean matched = false;
            for (LoadBalancerHostUsage actualUsage : actualUsageList) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String actualTimeStr = sdf.format(actualUsage.getPollTime().getTime());
                // Check differentiators then verify everything else.
                if (actualUsage.getLoadbalancerId() == lbId
                        && Objects.equals(actualTimeStr, pollTime)
                        && actualUsage.getTagsBitmask() == tags
                        && actualUsage.getHostId() == hostId) {
                    Assert.assertEquals(accountId.intValue(), actualUsage.getAccountId());
                    Assert.assertEquals(lbId.intValue(), actualUsage.getLoadbalancerId());
                    Assert.assertEquals(hostId.intValue(), actualUsage.getHostId());
                    Assert.assertEquals(incomingTransfer.longValue(), actualUsage.getIncomingTransfer());
                    Assert.assertEquals(incomingTransferSsl.longValue(), actualUsage.getIncomingTransferSsl());
                    Assert.assertEquals(outgoingTransfer.longValue(), actualUsage.getOutgoingTransfer());
                    Assert.assertEquals(outgoingTransferSsl.longValue(), actualUsage.getOutgoingTransferSsl());
                    Assert.assertEquals(ccs.intValue(), actualUsage.getConcurrentConnections());
                    Assert.assertEquals(ccsSsl.intValue(), actualUsage.getConcurrentConnectionsSsl());
                    Assert.assertEquals(numVips.intValue(), actualUsage.getNumVips());
                    Assert.assertEquals(tags.intValue(), actualUsage.getTagsBitmask());
                    Assert.assertEquals(eventType, actualUsage.getEventType());
                    Assert.assertEquals(pollTime, actualTimeStr);
                    matched = true;
                }
            }
            Assert.assertTrue(matched);
    }

    private static Calendar stringToCalendar(String calAsString) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTime(sdf.parse(calAsString));
        return cal;
    }
}
