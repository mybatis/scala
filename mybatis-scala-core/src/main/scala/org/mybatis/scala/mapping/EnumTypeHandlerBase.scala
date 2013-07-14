package org.mybatis.scala.mapping

import org.apache.ibatis.`type`.BaseTypeHandler
import java.sql.ResultSet
import java.sql.PreparedStatement
import java.sql.CallableStatement
import org.apache.ibatis.`type`.StringTypeHandler
import org.apache.ibatis.`type`.IntegerTypeHandler

trait EnumTypeHandlerBase[I] extends BaseTypeHandler[Enumeration#Value] {

  final def setNonNullParameter(ps: PreparedStatement, i: Int, parameter: Enumeration#Value, jdbcType: org.apache.ibatis.`type`.JdbcType) = {
    val x = toIntermediateRepr(parameter)
    intermediateTypeHandler.setParameter(ps, i, x, jdbcType)
  }

  final def getNullableResult(rs: ResultSet, columnName: String) = {
    Option(intermediateTypeHandler.getResult(rs, columnName)).map(fromIntermediateRepr).orNull
  }

  final def getNullableResult(rs: ResultSet, columnIndex: Int) = {
    Option(intermediateTypeHandler.getResult(rs, columnIndex)).map(fromIntermediateRepr).orNull
  }

  final def getNullableResult(cs: CallableStatement, columnIndex: Int) = {
    Option(intermediateTypeHandler.getResult(cs, columnIndex)).map(fromIntermediateRepr).orNull
  }
  protected def enumObj: Enumeration
  protected def intermediateTypeHandler: TypeHandler[I]
  protected def toIntermediateRepr(notNull: Enumeration#Value): I
  protected def fromIntermediateRepr(notNull: I): Enumeration#Value
}

trait EnumStrTypeHandler extends EnumTypeHandlerBase[String] {
  final protected val intermediateTypeHandler = new StringTypeHandler
  final protected def toIntermediateRepr(notNull: Enumeration#Value) = notNull.toString
  final protected def fromIntermediateRepr(notNull: String) = enumObj.withName(notNull)
}

trait EnumIntTypeHandler extends EnumTypeHandlerBase[Integer] {
  final protected val intermediateTypeHandler = new IntegerTypeHandler
  final protected def toIntermediateRepr(notNull: Enumeration#Value) = notNull.id
  final protected def fromIntermediateRepr(notNull: Integer) = enumObj(notNull.intValue)
}