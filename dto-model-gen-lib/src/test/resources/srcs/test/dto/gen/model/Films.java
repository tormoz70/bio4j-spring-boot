package test.dto.gen.model;

import io.swagger.annotations.ApiModelProperty;
import java.lang.Boolean;
import java.lang.Long;
import java.lang.String;

public final class Films {
  @ApiModelProperty(
      value = "ID",
      required = false,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Long filmId;

  @ApiModelProperty(
      value = "�������� ������",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String filmName;

  @ApiModelProperty(
      value = "��� �������",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String prodYear;

  @ApiModelProperty(
      value = "������",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String madein;

  @ApiModelProperty(
      value = "������",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String studia;

  @ApiModelProperty(
      value = "editable",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Boolean editable;

  @ApiModelProperty(
      value = "����",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String genre;

  @ApiModelProperty(
      value = "����� ��������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String bulletin;

  @ApiModelProperty(
      value = "������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String dubbing;

  @ApiModelProperty(
      value = "���������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String filmCategory;

  @ApiModelProperty(
      value = "���� ���������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String langOrig;

  @ApiModelProperty(
      value = "������������ ��������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String nameOrig;

  @ApiModelProperty(
      value = "������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String frmt;

  @ApiModelProperty(
      value = "���������� �����",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String sernum;

  @ApiModelProperty(
      value = "�������/����������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String colornot;

  @ApiModelProperty(
      value = "���������� ������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String partnum;

  @ApiModelProperty(
      value = "������ (������)",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String typeLength;

  @ApiModelProperty(
      value = "����������� �� ��������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String ageRestr;

  @ApiModelProperty(
      value = "������������ (�����)",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String durHours;

  @ApiModelProperty(
      value = "������������ (�����)",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String durMins;

  @ApiModelProperty(
      value = "����������� �� ��������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String viewRestr;

  @ApiModelProperty(
      value = "���������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String mproducer;

  @ApiModelProperty(
      value = "����������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String mscrwriters;

  @ApiModelProperty(
      value = "�������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String mdirector;

  @ApiModelProperty(
      value = "����������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String mcomposer;

  @ApiModelProperty(
      value = "��������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String moperator;

  @ApiModelProperty(
      value = "��������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String martist;

  @ApiModelProperty(
      value = "����������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String notetxt;

  @ApiModelProperty(
      value = "���������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private String annotxt;

  @ApiModelProperty(
      value = "������ �������",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private Boolean deleted;

  public void setFilmId(Long value) {
    this.filmId = value;
  }

  public Long getFilmId() {
    return this.filmId;
  }

  public void setFilmName(String value) {
    this.filmName = value;
  }

  public String getFilmName() {
    return this.filmName;
  }

  public void setProdYear(String value) {
    this.prodYear = value;
  }

  public String getProdYear() {
    return this.prodYear;
  }

  public void setMadein(String value) {
    this.madein = value;
  }

  public String getMadein() {
    return this.madein;
  }

  public void setStudia(String value) {
    this.studia = value;
  }

  public String getStudia() {
    return this.studia;
  }

  public void setEditable(Boolean value) {
    this.editable = value;
  }

  public Boolean getEditable() {
    return this.editable;
  }

  public void setGenre(String value) {
    this.genre = value;
  }

  public String getGenre() {
    return this.genre;
  }

  public void setBulletin(String value) {
    this.bulletin = value;
  }

  public String getBulletin() {
    return this.bulletin;
  }

  public void setDubbing(String value) {
    this.dubbing = value;
  }

  public String getDubbing() {
    return this.dubbing;
  }

  public void setFilmCategory(String value) {
    this.filmCategory = value;
  }

  public String getFilmCategory() {
    return this.filmCategory;
  }

  public void setLangOrig(String value) {
    this.langOrig = value;
  }

  public String getLangOrig() {
    return this.langOrig;
  }

  public void setNameOrig(String value) {
    this.nameOrig = value;
  }

  public String getNameOrig() {
    return this.nameOrig;
  }

  public void setFrmt(String value) {
    this.frmt = value;
  }

  public String getFrmt() {
    return this.frmt;
  }

  public void setSernum(String value) {
    this.sernum = value;
  }

  public String getSernum() {
    return this.sernum;
  }

  public void setColornot(String value) {
    this.colornot = value;
  }

  public String getColornot() {
    return this.colornot;
  }

  public void setPartnum(String value) {
    this.partnum = value;
  }

  public String getPartnum() {
    return this.partnum;
  }

  public void setTypeLength(String value) {
    this.typeLength = value;
  }

  public String getTypeLength() {
    return this.typeLength;
  }

  public void setAgeRestr(String value) {
    this.ageRestr = value;
  }

  public String getAgeRestr() {
    return this.ageRestr;
  }

  public void setDurHours(String value) {
    this.durHours = value;
  }

  public String getDurHours() {
    return this.durHours;
  }

  public void setDurMins(String value) {
    this.durMins = value;
  }

  public String getDurMins() {
    return this.durMins;
  }

  public void setViewRestr(String value) {
    this.viewRestr = value;
  }

  public String getViewRestr() {
    return this.viewRestr;
  }

  public void setMproducer(String value) {
    this.mproducer = value;
  }

  public String getMproducer() {
    return this.mproducer;
  }

  public void setMscrwriters(String value) {
    this.mscrwriters = value;
  }

  public String getMscrwriters() {
    return this.mscrwriters;
  }

  public void setMdirector(String value) {
    this.mdirector = value;
  }

  public String getMdirector() {
    return this.mdirector;
  }

  public void setMcomposer(String value) {
    this.mcomposer = value;
  }

  public String getMcomposer() {
    return this.mcomposer;
  }

  public void setMoperator(String value) {
    this.moperator = value;
  }

  public String getMoperator() {
    return this.moperator;
  }

  public void setMartist(String value) {
    this.martist = value;
  }

  public String getMartist() {
    return this.martist;
  }

  public void setNotetxt(String value) {
    this.notetxt = value;
  }

  public String getNotetxt() {
    return this.notetxt;
  }

  public void setAnnotxt(String value) {
    this.annotxt = value;
  }

  public String getAnnotxt() {
    return this.annotxt;
  }

  public void setDeleted(Boolean value) {
    this.deleted = value;
  }

  public Boolean getDeleted() {
    return this.deleted;
  }
}
