package org.openstack.atlas.atom.pojo;


import com.rackspace.docs.usage.lbaas.LoadBalancerUsage;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://docs.rackspace.com/usage/lbaas", name="loadBalancerUsageRecord")
public class LBaaSUsagePojo extends LoadBalancerUsage {

}