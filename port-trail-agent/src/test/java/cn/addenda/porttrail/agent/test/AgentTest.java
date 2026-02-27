package cn.addenda.porttrail.agent.test;

import cn.addenda.porttrail.agent.PortTrailAgent;

import java.io.File;

public class AgentTest {

  public static void main(String[] args) {
    System.out.println(PortTrailAgent.searchJars(new File("C:\\workspace\\project\\incubating\\port-trail\\dist")));
  }

}
