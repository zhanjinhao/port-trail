package cn.addenda.porttrail.agent.transform.match;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Match the class by the given super class or interfaces.
 * <p>
 * 这个类在使用之后，服务的启动时间会显著提升。因为他会遍历和解析每个类的父类/父接口。
 * 这个类在使用之后，可能产生can not resolve type的异常。需要配置{@link ProtectiveShieldMatcher}使用。
 */
public class HierarchyMatch implements IndirectMatch {

  private final String[] parentTypes;

  private HierarchyMatch(String[] parentTypes) {
    if (parentTypes == null || parentTypes.length == 0) {
      throw new IllegalArgumentException("parentTypes is null");
    }
    this.parentTypes = parentTypes;
  }

  @Override
  public ElementMatcher.Junction<? super TypeDescription> buildJunction() {
    ElementMatcher.Junction<? super TypeDescription> junction = null;
    for (String superTypeName : parentTypes) {
      if (junction == null) {
        junction = buildSuperClassMatcher(superTypeName);
      } else {
        junction = junction.and(buildSuperClassMatcher(superTypeName));
      }
    }
    junction = junction.and(not(isInterface()));
    return junction;
  }

  private ElementMatcher.Junction<? super TypeDescription> buildSuperClassMatcher(String superTypeName) {
    return hasSuperType(named(superTypeName));
  }

  @Override
  public boolean isMatch(TypeDescription typeDescription) {
    List<String> tmpList = new ArrayList<>(Arrays.asList(this.parentTypes));

    TypeList.Generic implInterfaces = typeDescription.getInterfaces();
    for (TypeDescription.Generic implInterface : implInterfaces) {
      matchHierarchyClass(implInterface, tmpList);
    }

    if (typeDescription.getSuperClass() != null) {
      matchHierarchyClass(typeDescription.getSuperClass(), tmpList);
    }

    return tmpList.isEmpty();
  }

  private void matchHierarchyClass(TypeDescription.Generic clazz, List<String> parentTypes) {
    parentTypes.remove(clazz.asRawType().getTypeName());
    if (parentTypes.isEmpty()) {
      return;
    }

    for (TypeDescription.Generic generic : clazz.getInterfaces()) {
      matchHierarchyClass(generic, parentTypes);
    }

    TypeDescription.Generic superClazz = clazz.getSuperClass();
    if (superClazz != null && !clazz.getTypeName().equals("java.lang.Object")) {
      matchHierarchyClass(superClazz, parentTypes);
    }

  }

  public static HierarchyMatch of(String... parentTypes) {
    return new HierarchyMatch(parentTypes);
  }

}
