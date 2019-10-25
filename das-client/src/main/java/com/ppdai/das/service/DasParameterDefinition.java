/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.ppdai.das.service;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.12.0)", date = "2019-10-25")
public class DasParameterDefinition implements org.apache.thrift.TBase<DasParameterDefinition, DasParameterDefinition._Fields>, java.io.Serializable, Cloneable, Comparable<DasParameterDefinition> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("DasParameterDefinition");

  private static final org.apache.thrift.protocol.TField DIRECTION_FIELD_DESC = new org.apache.thrift.protocol.TField("direction", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("name", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField INDEX_FIELD_DESC = new org.apache.thrift.protocol.TField("index", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField JDBC_TYPE_FIELD_DESC = new org.apache.thrift.protocol.TField("jdbcType", org.apache.thrift.protocol.TType.I32, (short)4);
  private static final org.apache.thrift.protocol.TField IN_VALUES_FIELD_DESC = new org.apache.thrift.protocol.TField("inValues", org.apache.thrift.protocol.TType.BOOL, (short)5);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new DasParameterDefinitionStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new DasParameterDefinitionTupleSchemeFactory();

  /**
   * 
   * @see DasParameterDirection
   */
  public @org.apache.thrift.annotation.Nullable DasParameterDirection direction; // required
  public @org.apache.thrift.annotation.Nullable String name; // optional
  public int index; // required
  public int jdbcType; // required
  public boolean inValues; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see DasParameterDirection
     */
    DIRECTION((short)1, "direction"),
    NAME((short)2, "name"),
    INDEX((short)3, "index"),
    JDBC_TYPE((short)4, "jdbcType"),
    IN_VALUES((short)5, "inValues");

    private static final java.util.Map<String, _Fields> byName = new java.util.HashMap<String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // DIRECTION
          return DIRECTION;
        case 2: // NAME
          return NAME;
        case 3: // INDEX
          return INDEX;
        case 4: // JDBC_TYPE
          return JDBC_TYPE;
        case 5: // IN_VALUES
          return IN_VALUES;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __INDEX_ISSET_ID = 0;
  private static final int __JDBCTYPE_ISSET_ID = 1;
  private static final int __INVALUES_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.NAME};
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.DIRECTION, new org.apache.thrift.meta_data.FieldMetaData("direction", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, DasParameterDirection.class)));
    tmpMap.put(_Fields.NAME, new org.apache.thrift.meta_data.FieldMetaData("name", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.INDEX, new org.apache.thrift.meta_data.FieldMetaData("index", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.JDBC_TYPE, new org.apache.thrift.meta_data.FieldMetaData("jdbcType", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.IN_VALUES, new org.apache.thrift.meta_data.FieldMetaData("inValues", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(DasParameterDefinition.class, metaDataMap);
  }

  public DasParameterDefinition() {
  }

  public DasParameterDefinition(
    DasParameterDirection direction,
    int index,
    int jdbcType,
    boolean inValues)
  {
    this();
    this.direction = direction;
    this.index = index;
    setIndexIsSet(true);
    this.jdbcType = jdbcType;
    setJdbcTypeIsSet(true);
    this.inValues = inValues;
    setInValuesIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public DasParameterDefinition(DasParameterDefinition other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetDirection()) {
      this.direction = other.direction;
    }
    if (other.isSetName()) {
      this.name = other.name;
    }
    this.index = other.index;
    this.jdbcType = other.jdbcType;
    this.inValues = other.inValues;
  }

  public DasParameterDefinition deepCopy() {
    return new DasParameterDefinition(this);
  }

  @Override
  public void clear() {
    this.direction = null;
    this.name = null;
    setIndexIsSet(false);
    this.index = 0;
    setJdbcTypeIsSet(false);
    this.jdbcType = 0;
    setInValuesIsSet(false);
    this.inValues = false;
  }

  /**
   * 
   * @see DasParameterDirection
   */
  @org.apache.thrift.annotation.Nullable
  public DasParameterDirection getDirection() {
    return this.direction;
  }

  /**
   * 
   * @see DasParameterDirection
   */
  public DasParameterDefinition setDirection(@org.apache.thrift.annotation.Nullable DasParameterDirection direction) {
    this.direction = direction;
    return this;
  }

  public void unsetDirection() {
    this.direction = null;
  }

  /** Returns true if field direction is set (has been assigned a value) and false otherwise */
  public boolean isSetDirection() {
    return this.direction != null;
  }

  public void setDirectionIsSet(boolean value) {
    if (!value) {
      this.direction = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public String getName() {
    return this.name;
  }

  public DasParameterDefinition setName(@org.apache.thrift.annotation.Nullable String name) {
    this.name = name;
    return this;
  }

  public void unsetName() {
    this.name = null;
  }

  /** Returns true if field name is set (has been assigned a value) and false otherwise */
  public boolean isSetName() {
    return this.name != null;
  }

  public void setNameIsSet(boolean value) {
    if (!value) {
      this.name = null;
    }
  }

  public int getIndex() {
    return this.index;
  }

  public DasParameterDefinition setIndex(int index) {
    this.index = index;
    setIndexIsSet(true);
    return this;
  }

  public void unsetIndex() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __INDEX_ISSET_ID);
  }

  /** Returns true if field index is set (has been assigned a value) and false otherwise */
  public boolean isSetIndex() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __INDEX_ISSET_ID);
  }

  public void setIndexIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __INDEX_ISSET_ID, value);
  }

  public int getJdbcType() {
    return this.jdbcType;
  }

  public DasParameterDefinition setJdbcType(int jdbcType) {
    this.jdbcType = jdbcType;
    setJdbcTypeIsSet(true);
    return this;
  }

  public void unsetJdbcType() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __JDBCTYPE_ISSET_ID);
  }

  /** Returns true if field jdbcType is set (has been assigned a value) and false otherwise */
  public boolean isSetJdbcType() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __JDBCTYPE_ISSET_ID);
  }

  public void setJdbcTypeIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __JDBCTYPE_ISSET_ID, value);
  }

  public boolean isInValues() {
    return this.inValues;
  }

  public DasParameterDefinition setInValues(boolean inValues) {
    this.inValues = inValues;
    setInValuesIsSet(true);
    return this;
  }

  public void unsetInValues() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __INVALUES_ISSET_ID);
  }

  /** Returns true if field inValues is set (has been assigned a value) and false otherwise */
  public boolean isSetInValues() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __INVALUES_ISSET_ID);
  }

  public void setInValuesIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __INVALUES_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable Object value) {
    switch (field) {
    case DIRECTION:
      if (value == null) {
        unsetDirection();
      } else {
        setDirection((DasParameterDirection)value);
      }
      break;

    case NAME:
      if (value == null) {
        unsetName();
      } else {
        setName((String)value);
      }
      break;

    case INDEX:
      if (value == null) {
        unsetIndex();
      } else {
        setIndex((Integer)value);
      }
      break;

    case JDBC_TYPE:
      if (value == null) {
        unsetJdbcType();
      } else {
        setJdbcType((Integer)value);
      }
      break;

    case IN_VALUES:
      if (value == null) {
        unsetInValues();
      } else {
        setInValues((Boolean)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public Object getFieldValue(_Fields field) {
    switch (field) {
    case DIRECTION:
      return getDirection();

    case NAME:
      return getName();

    case INDEX:
      return getIndex();

    case JDBC_TYPE:
      return getJdbcType();

    case IN_VALUES:
      return isInValues();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case DIRECTION:
      return isSetDirection();
    case NAME:
      return isSetName();
    case INDEX:
      return isSetIndex();
    case JDBC_TYPE:
      return isSetJdbcType();
    case IN_VALUES:
      return isSetInValues();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof DasParameterDefinition)
      return this.equals((DasParameterDefinition)that);
    return false;
  }

  public boolean equals(DasParameterDefinition that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_direction = true && this.isSetDirection();
    boolean that_present_direction = true && that.isSetDirection();
    if (this_present_direction || that_present_direction) {
      if (!(this_present_direction && that_present_direction))
        return false;
      if (!this.direction.equals(that.direction))
        return false;
    }

    boolean this_present_name = true && this.isSetName();
    boolean that_present_name = true && that.isSetName();
    if (this_present_name || that_present_name) {
      if (!(this_present_name && that_present_name))
        return false;
      if (!this.name.equals(that.name))
        return false;
    }

    boolean this_present_index = true;
    boolean that_present_index = true;
    if (this_present_index || that_present_index) {
      if (!(this_present_index && that_present_index))
        return false;
      if (this.index != that.index)
        return false;
    }

    boolean this_present_jdbcType = true;
    boolean that_present_jdbcType = true;
    if (this_present_jdbcType || that_present_jdbcType) {
      if (!(this_present_jdbcType && that_present_jdbcType))
        return false;
      if (this.jdbcType != that.jdbcType)
        return false;
    }

    boolean this_present_inValues = true;
    boolean that_present_inValues = true;
    if (this_present_inValues || that_present_inValues) {
      if (!(this_present_inValues && that_present_inValues))
        return false;
      if (this.inValues != that.inValues)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetDirection()) ? 131071 : 524287);
    if (isSetDirection())
      hashCode = hashCode * 8191 + direction.getValue();

    hashCode = hashCode * 8191 + ((isSetName()) ? 131071 : 524287);
    if (isSetName())
      hashCode = hashCode * 8191 + name.hashCode();

    hashCode = hashCode * 8191 + index;

    hashCode = hashCode * 8191 + jdbcType;

    hashCode = hashCode * 8191 + ((inValues) ? 131071 : 524287);

    return hashCode;
  }

  @Override
  public int compareTo(DasParameterDefinition other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetDirection()).compareTo(other.isSetDirection());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDirection()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.direction, other.direction);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetName()).compareTo(other.isSetName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.name, other.name);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIndex()).compareTo(other.isSetIndex());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIndex()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.index, other.index);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetJdbcType()).compareTo(other.isSetJdbcType());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetJdbcType()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.jdbcType, other.jdbcType);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetInValues()).compareTo(other.isSetInValues());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetInValues()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.inValues, other.inValues);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("DasParameterDefinition(");
    boolean first = true;

    sb.append("direction:");
    if (this.direction == null) {
      sb.append("null");
    } else {
      sb.append(this.direction);
    }
    first = false;
    if (isSetName()) {
      if (!first) sb.append(", ");
      sb.append("name:");
      if (this.name == null) {
        sb.append("null");
      } else {
        sb.append(this.name);
      }
      first = false;
    }
    if (!first) sb.append(", ");
    sb.append("index:");
    sb.append(this.index);
    first = false;
    if (!first) sb.append(", ");
    sb.append("jdbcType:");
    sb.append(this.jdbcType);
    first = false;
    if (!first) sb.append(", ");
    sb.append("inValues:");
    sb.append(this.inValues);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (direction == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'direction' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'index' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'jdbcType' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'inValues' because it's a primitive and you chose the non-beans generator.
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class DasParameterDefinitionStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public DasParameterDefinitionStandardScheme getScheme() {
      return new DasParameterDefinitionStandardScheme();
    }
  }

  private static class DasParameterDefinitionStandardScheme extends org.apache.thrift.scheme.StandardScheme<DasParameterDefinition> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, DasParameterDefinition struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // DIRECTION
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.direction = DasParameterDirection.findByValue(iprot.readI32());
              struct.setDirectionIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.name = iprot.readString();
              struct.setNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // INDEX
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.index = iprot.readI32();
              struct.setIndexIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // JDBC_TYPE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.jdbcType = iprot.readI32();
              struct.setJdbcTypeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // IN_VALUES
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.inValues = iprot.readBool();
              struct.setInValuesIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      if (!struct.isSetIndex()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'index' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetJdbcType()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'jdbcType' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetInValues()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'inValues' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, DasParameterDefinition struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.direction != null) {
        oprot.writeFieldBegin(DIRECTION_FIELD_DESC);
        oprot.writeI32(struct.direction.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.name != null) {
        if (struct.isSetName()) {
          oprot.writeFieldBegin(NAME_FIELD_DESC);
          oprot.writeString(struct.name);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldBegin(INDEX_FIELD_DESC);
      oprot.writeI32(struct.index);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(JDBC_TYPE_FIELD_DESC);
      oprot.writeI32(struct.jdbcType);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(IN_VALUES_FIELD_DESC);
      oprot.writeBool(struct.inValues);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class DasParameterDefinitionTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public DasParameterDefinitionTupleScheme getScheme() {
      return new DasParameterDefinitionTupleScheme();
    }
  }

  private static class DasParameterDefinitionTupleScheme extends org.apache.thrift.scheme.TupleScheme<DasParameterDefinition> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, DasParameterDefinition struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeI32(struct.direction.getValue());
      oprot.writeI32(struct.index);
      oprot.writeI32(struct.jdbcType);
      oprot.writeBool(struct.inValues);
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetName()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetName()) {
        oprot.writeString(struct.name);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, DasParameterDefinition struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.direction = DasParameterDirection.findByValue(iprot.readI32());
      struct.setDirectionIsSet(true);
      struct.index = iprot.readI32();
      struct.setIndexIsSet(true);
      struct.jdbcType = iprot.readI32();
      struct.setJdbcTypeIsSet(true);
      struct.inValues = iprot.readBool();
      struct.setInValuesIsSet(true);
      java.util.BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.name = iprot.readString();
        struct.setNameIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

