package com.brclys.thct.openApiGenSrc.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * CreateUserRequestAddress
 */

@JsonTypeName("CreateUserRequest_address")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-29T12:22:03.964972322+01:00[Europe/London]", comments = "Generator version: 7.14.0")
public class CreateUserRequestAddress {

  private String line1;

  private @Nullable String line2;

  private @Nullable String line3;

  private String town;

  private String county;

  private String postcode;

  public CreateUserRequestAddress() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CreateUserRequestAddress(String line1, String town, String county, String postcode) {
    this.line1 = line1;
    this.town = town;
    this.county = county;
    this.postcode = postcode;
  }

  public CreateUserRequestAddress line1(String line1) {
    this.line1 = line1;
    return this;
  }

  /**
   * Get line1
   * @return line1
   */
  @NotNull 
  @Schema(name = "line1", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("line1")
  public String getLine1() {
    return line1;
  }

  public void setLine1(String line1) {
    this.line1 = line1;
  }

  public CreateUserRequestAddress line2(@Nullable String line2) {
    this.line2 = line2;
    return this;
  }

  /**
   * Get line2
   * @return line2
   */
  
  @Schema(name = "line2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("line2")
  public @Nullable String getLine2() {
    return line2;
  }

  public void setLine2(@Nullable String line2) {
    this.line2 = line2;
  }

  public CreateUserRequestAddress line3(@Nullable String line3) {
    this.line3 = line3;
    return this;
  }

  /**
   * Get line3
   * @return line3
   */
  
  @Schema(name = "line3", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("line3")
  public @Nullable String getLine3() {
    return line3;
  }

  public void setLine3(@Nullable String line3) {
    this.line3 = line3;
  }

  public CreateUserRequestAddress town(String town) {
    this.town = town;
    return this;
  }

  /**
   * Get town
   * @return town
   */
  @NotNull 
  @Schema(name = "town", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("town")
  public String getTown() {
    return town;
  }

  public void setTown(String town) {
    this.town = town;
  }

  public CreateUserRequestAddress county(String county) {
    this.county = county;
    return this;
  }

  /**
   * Get county
   * @return county
   */
  @NotNull 
  @Schema(name = "county", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("county")
  public String getCounty() {
    return county;
  }

  public void setCounty(String county) {
    this.county = county;
  }

  public CreateUserRequestAddress postcode(String postcode) {
    this.postcode = postcode;
    return this;
  }

  /**
   * Get postcode
   * @return postcode
   */
  @NotNull 
  @Schema(name = "postcode", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("postcode")
  public String getPostcode() {
    return postcode;
  }

  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateUserRequestAddress createUserRequestAddress = (CreateUserRequestAddress) o;
    return Objects.equals(this.line1, createUserRequestAddress.line1) &&
        Objects.equals(this.line2, createUserRequestAddress.line2) &&
        Objects.equals(this.line3, createUserRequestAddress.line3) &&
        Objects.equals(this.town, createUserRequestAddress.town) &&
        Objects.equals(this.county, createUserRequestAddress.county) &&
        Objects.equals(this.postcode, createUserRequestAddress.postcode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(line1, line2, line3, town, county, postcode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateUserRequestAddress {\n");
    sb.append("    line1: ").append(toIndentedString(line1)).append("\n");
    sb.append("    line2: ").append(toIndentedString(line2)).append("\n");
    sb.append("    line3: ").append(toIndentedString(line3)).append("\n");
    sb.append("    town: ").append(toIndentedString(town)).append("\n");
    sb.append("    county: ").append(toIndentedString(county)).append("\n");
    sb.append("    postcode: ").append(toIndentedString(postcode)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

