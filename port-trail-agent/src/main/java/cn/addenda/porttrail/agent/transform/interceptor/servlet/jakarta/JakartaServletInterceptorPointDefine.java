//package cn.addenda.porttrail.agent.transform.interceptor.servlet.jakarta;
//
//import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPoint;
//import cn.addenda.porttrail.agent.transform.interceptor.InterceptorPointDefine;
//import cn.addenda.porttrail.agent.transform.match.ClassMatch;
//import cn.addenda.porttrail.agent.transform.match.NameMatch;
//import cn.addenda.porttrail.common.util.ArrayUtils;
//
//import java.util.List;
//
//public class JakartaServletInterceptorPointDefine implements InterceptorPointDefine {
//
//  @Override
//  public ClassMatch getEnhancedClass() {
//    return NameMatch.of("jakarta.servlet.http.HttpServlet");
//  }
//
//  @Override
//  public List<InterceptorPoint> getInterceptorPointList() {
//    return ArrayUtils.asArrayList(new JakartaServletInterceptorPoint());
//  }
//
//}
