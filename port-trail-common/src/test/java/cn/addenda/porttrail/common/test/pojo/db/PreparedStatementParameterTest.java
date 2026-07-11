package cn.addenda.porttrail.common.test.pojo.db;

import cn.addenda.porttrail.common.pojo.db.bo.PreparedStatementParameter;
import cn.addenda.porttrail.common.pojo.db.dto.PreparedStatementParameterDto;
import cn.addenda.porttrail.common.tuple.Binary;
import cn.addenda.porttrail.common.tuple.Ternary;
import cn.addenda.porttrail.common.tuple.Unary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class PreparedStatementParameterTest {

  @Test
  void testDtoFromBo_unary() {
    PreparedStatementParameter bo = new PreparedStatementParameter();
    bo.setOrderInStatement(1);
    bo.setOrderInConnection(1);
    bo.set(0, "setString", Unary.of("hello"));
    bo.set(1, "setInt", Unary.of(42));

    PreparedStatementParameterDto dto = new PreparedStatementParameterDto(bo);

    Assertions.assertEquals(1, dto.getOrderInStatement());
    Assertions.assertEquals(1, dto.getOrderInConnection());
    Assertions.assertEquals(2, dto.getParameterTypeList().size());
    Assertions.assertEquals(Unary.class, dto.getParameterTypeList().get(0));
    Assertions.assertEquals(Unary.class, dto.getParameterTypeList().get(1));
    Assertions.assertEquals(2, dto.getParameterValueList().size());
    Assertions.assertEquals(2, dto.getSetMethodList().size());
    Assertions.assertEquals("setString", dto.getSetMethodList().get(0));
    Assertions.assertEquals("setInt", dto.getSetMethodList().get(1));
  }

  @Test
  void testBoFromDto_unary() {
    PreparedStatementParameterDto dto = new PreparedStatementParameterDto();
    dto.setOrderInStatement(1);
    dto.setOrderInConnection(1);
    dto.setSetMethodList(Arrays.asList("setString", "setInt"));

    PreparedStatementParameter temp = new PreparedStatementParameter();
    temp.set(0, "setString", Unary.of("world"));
    temp.set(1, "setInt", Unary.of(100));
    PreparedStatementParameterDto tempDto = new PreparedStatementParameterDto(temp);
    dto.setParameterTypeList(tempDto.getParameterTypeList());
    dto.setParameterValueList(tempDto.getParameterValueList());

    PreparedStatementParameter bo = new PreparedStatementParameter(dto);

    Assertions.assertEquals(1, bo.getOrderInStatement());
    Assertions.assertEquals(1, bo.getOrderInConnection());
    Assertions.assertEquals(2, bo.getCapacity());
    Assertions.assertEquals(2, bo.getParameterList().size());
    Assertions.assertEquals("setString", bo.getSetMethodList().get(0));
    Assertions.assertEquals("setInt", bo.getSetMethodList().get(1));
    Assertions.assertTrue(bo.getParameterList().get(0) instanceof Unary);
    Assertions.assertEquals("world", ((Unary<?>) bo.getParameterList().get(0)).getF1());
    Assertions.assertTrue(bo.getParameterList().get(1) instanceof Unary);
    Assertions.assertEquals(100, ((Unary<?>) bo.getParameterList().get(1)).getF1());
  }

  @Test
  void testDtoFromBo_binary() {
    PreparedStatementParameter bo = new PreparedStatementParameter();
    bo.setOrderInStatement(2);
    bo.setOrderInConnection(3);
    bo.set(0, "setObject", Binary.of("key", 999L));

    PreparedStatementParameterDto dto = new PreparedStatementParameterDto(bo);

    Assertions.assertEquals(1, dto.getParameterTypeList().size());
    Assertions.assertEquals(Binary.class, dto.getParameterTypeList().get(0));
    Assertions.assertEquals(2, dto.getParameterValueList().size());
  }

  @Test
  void testBoFromDto_binary() {
    PreparedStatementParameterDto dto = new PreparedStatementParameterDto();
    dto.setOrderInStatement(2);
    dto.setOrderInConnection(3);
    dto.setSetMethodList(Arrays.asList("setObject"));

    PreparedStatementParameter temp = new PreparedStatementParameter();
    temp.set(0, "setObject", Binary.of("key", 999L));
    PreparedStatementParameterDto tempDto = new PreparedStatementParameterDto(temp);
    dto.setParameterTypeList(tempDto.getParameterTypeList());
    dto.setParameterValueList(tempDto.getParameterValueList());

    PreparedStatementParameter bo = new PreparedStatementParameter(dto);

    Assertions.assertEquals(1, bo.getParameterList().size());
    Assertions.assertTrue(bo.getParameterList().get(0) instanceof Binary);
    Binary<?, ?> binary = (Binary<?, ?>) bo.getParameterList().get(0);
    Assertions.assertEquals("key", binary.getF1());
    Assertions.assertEquals(999L, binary.getF2());
  }

  @Test
  void testDtoFromBo_ternary() {
    PreparedStatementParameter bo = new PreparedStatementParameter();
    bo.set(0, "setValues", Ternary.of("a", 1, 2.5));

    PreparedStatementParameterDto dto = new PreparedStatementParameterDto(bo);

    Assertions.assertEquals(1, dto.getParameterTypeList().size());
    Assertions.assertEquals(Ternary.class, dto.getParameterTypeList().get(0));
    Assertions.assertEquals(3, dto.getParameterValueList().size());
  }

  @Test
  void testBoFromDto_ternary() {
    PreparedStatementParameterDto dto = new PreparedStatementParameterDto();
    dto.setSetMethodList(Arrays.asList("setValues"));

    PreparedStatementParameter temp = new PreparedStatementParameter();
    temp.set(0, "setValues", Ternary.of("a", 1, 2.5));
    PreparedStatementParameterDto tempDto = new PreparedStatementParameterDto(temp);
    dto.setParameterTypeList(tempDto.getParameterTypeList());
    dto.setParameterValueList(tempDto.getParameterValueList());

    PreparedStatementParameter bo = new PreparedStatementParameter(dto);

    Assertions.assertEquals(1, bo.getParameterList().size());
    Assertions.assertTrue(bo.getParameterList().get(0) instanceof Ternary);
    Ternary<?, ?, ?> ternary = (Ternary<?, ?, ?>) bo.getParameterList().get(0);
    Assertions.assertEquals("a", ternary.getF1());
    Assertions.assertEquals(1, ternary.getF2());
    Assertions.assertEquals(2.5, ternary.getF3());
  }

  @Test
  void testWithNullParameter() {
    PreparedStatementParameter bo = new PreparedStatementParameter();
    bo.set(0, "setNull", Unary.of((Object) null));

    PreparedStatementParameterDto dto = new PreparedStatementParameterDto(bo);

    Assertions.assertEquals(1, dto.getParameterValueList().size());
    Assertions.assertNull(dto.getParameterValueList().get(0));
  }

  @Test
  void testToObj_unsupportedParameter() {
    byte[] unsupported = PreparedStatementParameter.getUN_SUPPORTED_PARAMETER();
    Assertions.assertEquals(-1, unsupported[0]);
    Assertions.assertEquals(1, unsupported.length);
  }

  @Test
  void testFullRoundTrip() {
    PreparedStatementParameter bo1 = new PreparedStatementParameter();
    bo1.setOrderInStatement(1);
    bo1.setOrderInConnection(1);
    bo1.set(0, "setString", Unary.of("hello"));
    bo1.set(1, "setInt", Unary.of(42));
    bo1.set(2, "setObject", Binary.of("key", 999L));
    bo1.set(3, "setValues", Ternary.of("a", 1, 2.5));

    PreparedStatementParameterDto dto1 = new PreparedStatementParameterDto(bo1);
    PreparedStatementParameter bo2 = new PreparedStatementParameter(dto1);
    PreparedStatementParameterDto dto2 = new PreparedStatementParameterDto(bo2);

    Assertions.assertEquals(bo1.getOrderInStatement(), bo2.getOrderInStatement());
    Assertions.assertEquals(bo1.getOrderInConnection(), bo2.getOrderInConnection());
    Assertions.assertEquals(bo1.getCapacity(), bo2.getCapacity());
    Assertions.assertEquals(bo1.getSetMethodList(), bo2.getSetMethodList());
    Assertions.assertEquals(bo1.getParameterList(), bo2.getParameterList());

    Assertions.assertEquals(dto1.getOrderInStatement(), dto2.getOrderInStatement());
    Assertions.assertEquals(dto1.getOrderInConnection(), dto2.getOrderInConnection());
    Assertions.assertEquals(dto1.getSetMethodList(), dto2.getSetMethodList());
    Assertions.assertEquals(dto1.getParameterTypeList(), dto2.getParameterTypeList());
    Assertions.assertEquals(dto1.getParameterValueList().size(), dto2.getParameterValueList().size());
    for (int i = 0; i < dto1.getParameterValueList().size(); i++) {
      Assertions.assertArrayEquals(dto1.getParameterValueList().get(i), dto2.getParameterValueList().get(i));
    }
  }

  @Test
  void testUnsupportedParameterRoundTrip() {
    PreparedStatementParameter bo = new PreparedStatementParameter();
    bo.set(0, "setObject", Unary.of(new Object()));

    PreparedStatementParameterDto dto1 = new PreparedStatementParameterDto(bo);
    Assertions.assertEquals(1, dto1.getParameterValueList().size());
    Assertions.assertArrayEquals(
            PreparedStatementParameter.getUN_SUPPORTED_PARAMETER(),
            dto1.getParameterValueList().get(0));

    PreparedStatementParameter bo2 = new PreparedStatementParameter(dto1);
    Assertions.assertEquals(1, bo2.getParameterList().size());
    Assertions.assertTrue(bo2.getParameterList().get(0) instanceof Unary);
    Assertions.assertNull(((Unary<?>) bo2.getParameterList().get(0)).getF1());

    PreparedStatementParameterDto dto2 = new PreparedStatementParameterDto(bo2);
    Assertions.assertEquals(1, dto2.getParameterValueList().size());
    Assertions.assertNull(dto2.getParameterValueList().get(0));
  }

  @Test
  void testDeepClone() {
    PreparedStatementParameter bo = new PreparedStatementParameter();
    bo.setOrderInStatement(10);
    bo.setOrderInConnection(20);
    bo.set(0, "setString", Unary.of("original"));

    PreparedStatementParameter clone = bo.deepClone();

    Assertions.assertEquals(bo.getOrderInStatement(), clone.getOrderInStatement());
    Assertions.assertEquals(bo.getOrderInConnection(), clone.getOrderInConnection());
    Assertions.assertEquals(bo.getCapacity(), clone.getCapacity());
    Assertions.assertEquals(bo.getSetMethodList().size(), clone.getSetMethodList().size());
    Assertions.assertEquals(bo.getSetMethodList().get(0), clone.getSetMethodList().get(0));
    Assertions.assertEquals(bo.getParameterList().size(), clone.getParameterList().size());
  }

}
