package test.dto.gen.model.sub;

import io.swagger.annotations.ApiModelProperty;
import java.lang.Boolean;
import java.lang.Long;
import java.lang.String;
import java.util.Date;

public final class Pus {
  @ApiModelProperty(
      value = "ID ПУ",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Long puId;

  @ApiModelProperty(
      value = "film_id",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Long filmId;

  @ApiModelProperty(
      value = "Номер ПУ",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String puNum;

  @ApiModelProperty(
      value = "Начало ПУ",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Date issued;

  @ApiModelProperty(
      value = "Категория",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String filmCategory;

  @ApiModelProperty(
      value = "Категория проката",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String rentCategory;

  @ApiModelProperty(
      value = "Окончание ПУ",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Date rentEnd;

  @ApiModelProperty(
      value = "Описание",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String rentDesc;

  @ApiModelProperty(
      value = "Правообладатели",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String distributors;

  @ApiModelProperty(
      value = "Владельцы",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String owners;

  @ApiModelProperty(
      value = "contr_num",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String contrNum;

  @ApiModelProperty(
      value = "contr_date",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Date contrDate;

  @ApiModelProperty(
      value = "Дата заполнения",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Date creDate;

  @ApiModelProperty(
      value = "Название фильма",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String rentName;

  @ApiModelProperty(
      value = "Название фильма (en)",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String nameOrig;

  @ApiModelProperty(
      value = "Страна производства (en)",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String madeinOrig;

  @ApiModelProperty(
      value = "Язык оригинала",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String langOrig;

  @ApiModelProperty(
      value = "Год выпуска",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String prodYear;

  @ApiModelProperty(
      value = "Страна производства",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String madein;

  @ApiModelProperty(
      value = "Студия",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String studia;

  @ApiModelProperty(
      value = "Жанр",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String genre;

  @ApiModelProperty(
      value = "age_restr",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Long ageRestr;

  @ApiModelProperty(
      value = "Ограничения",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String viewRestr;

  @ApiModelProperty(
      value = "Продюссеры",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String producer;

  @ApiModelProperty(
      value = "Сценартсты",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String scrwriters;

  @ApiModelProperty(
      value = "Режисеры",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String director;

  @ApiModelProperty(
      value = "Сомпозиторы",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String composer;

  @ApiModelProperty(
      value = "Операторы",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String operator;

  @ApiModelProperty(
      value = "Художники",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String artist;

  @ApiModelProperty(
      value = "Описание ПУ",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String puDesc;

  @ApiModelProperty(
      value = "editable",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Boolean editable;

  @ApiModelProperty(
      value = "pu_num_perm",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String puNumPerm;

  @ApiModelProperty(
      value = "date_from_perm",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Date dateFromPerm;

  @ApiModelProperty(
      value = "date_to_perm",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Date dateToPerm;

  @ApiModelProperty(
      value = "Дата релиза",
      required = false,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.AUTO
  )
  private Date startdate;

  @ApiModelProperty(
      value = "Дистрибьюторы",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String distribs;

  @ApiModelProperty(
      value = "distrib_ids",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String distribIds;

  @ApiModelProperty(
      value = "Опубликован на сайте МКРФ",
      required = false,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Boolean siteCheck;

  @ApiModelProperty(
      value = "Продолжительность",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String fduration;

  @ApiModelProperty(
      value = "Продолжительность (мин)",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Long mduration;

  @ApiModelProperty(
      value = "Кол-во серий",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Long sernum;

  @ApiModelProperty(
      value = "pogu_id",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Long poguId;

  @ApiModelProperty(
      value = "Удален",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Boolean deleted;

  @ApiModelProperty(
      value = "Дата удаления",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Date deletedDate;

  @ApiModelProperty(
      value = "Примечания",
      required = true,
      hidden = false,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private String notetxt;

  @ApiModelProperty(
      value = "prefrom",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Date prefrom;

  @ApiModelProperty(
      value = "preto",
      required = false,
      hidden = true,
      accessMode = ApiModelProperty.AccessMode.READ_ONLY
  )
  private Date preto;

  public void setPuId(Long value) {
    this.puId = value;
  }

  public Long getPuId() {
    return this.puId;
  }

  public void setFilmId(Long value) {
    this.filmId = value;
  }

  public Long getFilmId() {
    return this.filmId;
  }

  public void setPuNum(String value) {
    this.puNum = value;
  }

  public String getPuNum() {
    return this.puNum;
  }

  public void setIssued(Date value) {
    this.issued = value;
  }

  public Date getIssued() {
    return this.issued;
  }

  public void setFilmCategory(String value) {
    this.filmCategory = value;
  }

  public String getFilmCategory() {
    return this.filmCategory;
  }

  public void setRentCategory(String value) {
    this.rentCategory = value;
  }

  public String getRentCategory() {
    return this.rentCategory;
  }

  public void setRentEnd(Date value) {
    this.rentEnd = value;
  }

  public Date getRentEnd() {
    return this.rentEnd;
  }

  public void setRentDesc(String value) {
    this.rentDesc = value;
  }

  public String getRentDesc() {
    return this.rentDesc;
  }

  public void setDistributors(String value) {
    this.distributors = value;
  }

  public String getDistributors() {
    return this.distributors;
  }

  public void setOwners(String value) {
    this.owners = value;
  }

  public String getOwners() {
    return this.owners;
  }

  public void setContrNum(String value) {
    this.contrNum = value;
  }

  public String getContrNum() {
    return this.contrNum;
  }

  public void setContrDate(Date value) {
    this.contrDate = value;
  }

  public Date getContrDate() {
    return this.contrDate;
  }

  public void setCreDate(Date value) {
    this.creDate = value;
  }

  public Date getCreDate() {
    return this.creDate;
  }

  public void setRentName(String value) {
    this.rentName = value;
  }

  public String getRentName() {
    return this.rentName;
  }

  public void setNameOrig(String value) {
    this.nameOrig = value;
  }

  public String getNameOrig() {
    return this.nameOrig;
  }

  public void setMadeinOrig(String value) {
    this.madeinOrig = value;
  }

  public String getMadeinOrig() {
    return this.madeinOrig;
  }

  public void setLangOrig(String value) {
    this.langOrig = value;
  }

  public String getLangOrig() {
    return this.langOrig;
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

  public void setGenre(String value) {
    this.genre = value;
  }

  public String getGenre() {
    return this.genre;
  }

  public void setAgeRestr(Long value) {
    this.ageRestr = value;
  }

  public Long getAgeRestr() {
    return this.ageRestr;
  }

  public void setViewRestr(String value) {
    this.viewRestr = value;
  }

  public String getViewRestr() {
    return this.viewRestr;
  }

  public void setProducer(String value) {
    this.producer = value;
  }

  public String getProducer() {
    return this.producer;
  }

  public void setScrwriters(String value) {
    this.scrwriters = value;
  }

  public String getScrwriters() {
    return this.scrwriters;
  }

  public void setDirector(String value) {
    this.director = value;
  }

  public String getDirector() {
    return this.director;
  }

  public void setComposer(String value) {
    this.composer = value;
  }

  public String getComposer() {
    return this.composer;
  }

  public void setOperator(String value) {
    this.operator = value;
  }

  public String getOperator() {
    return this.operator;
  }

  public void setArtist(String value) {
    this.artist = value;
  }

  public String getArtist() {
    return this.artist;
  }

  public void setPuDesc(String value) {
    this.puDesc = value;
  }

  public String getPuDesc() {
    return this.puDesc;
  }

  public void setEditable(Boolean value) {
    this.editable = value;
  }

  public Boolean getEditable() {
    return this.editable;
  }

  public void setPuNumPerm(String value) {
    this.puNumPerm = value;
  }

  public String getPuNumPerm() {
    return this.puNumPerm;
  }

  public void setDateFromPerm(Date value) {
    this.dateFromPerm = value;
  }

  public Date getDateFromPerm() {
    return this.dateFromPerm;
  }

  public void setDateToPerm(Date value) {
    this.dateToPerm = value;
  }

  public Date getDateToPerm() {
    return this.dateToPerm;
  }

  public void setStartdate(Date value) {
    this.startdate = value;
  }

  public Date getStartdate() {
    return this.startdate;
  }

  public void setDistribs(String value) {
    this.distribs = value;
  }

  public String getDistribs() {
    return this.distribs;
  }

  public void setDistribIds(String value) {
    this.distribIds = value;
  }

  public String getDistribIds() {
    return this.distribIds;
  }

  public void setSiteCheck(Boolean value) {
    this.siteCheck = value;
  }

  public Boolean getSiteCheck() {
    return this.siteCheck;
  }

  public void setFduration(String value) {
    this.fduration = value;
  }

  public String getFduration() {
    return this.fduration;
  }

  public void setMduration(Long value) {
    this.mduration = value;
  }

  public Long getMduration() {
    return this.mduration;
  }

  public void setSernum(Long value) {
    this.sernum = value;
  }

  public Long getSernum() {
    return this.sernum;
  }

  public void setPoguId(Long value) {
    this.poguId = value;
  }

  public Long getPoguId() {
    return this.poguId;
  }

  public void setDeleted(Boolean value) {
    this.deleted = value;
  }

  public Boolean getDeleted() {
    return this.deleted;
  }

  public void setDeletedDate(Date value) {
    this.deletedDate = value;
  }

  public Date getDeletedDate() {
    return this.deletedDate;
  }

  public void setNotetxt(String value) {
    this.notetxt = value;
  }

  public String getNotetxt() {
    return this.notetxt;
  }

  public void setPrefrom(Date value) {
    this.prefrom = value;
  }

  public Date getPrefrom() {
    return this.prefrom;
  }

  public void setPreto(Date value) {
    this.preto = value;
  }

  public Date getPreto() {
    return this.preto;
  }
}
