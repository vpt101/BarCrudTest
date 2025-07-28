package com.brclys.thct.openApiGenSrc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * UpdateBankAccountRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-29T12:22:03.964972322+01:00[Europe/London]", comments = "Generator version: 7.14.0")
public class UpdateBankAccountRequest {

  private @Nullable String name;

  /**
   * Gets or Sets accountType
   */
  public enum AccountTypeEnum {
    PERSONAL("personal");

    private final String value;

    AccountTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static AccountTypeEnum fromValue(String value) {
      for (AccountTypeEnum b : AccountTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable AccountTypeEnum accountType;

  public UpdateBankAccountRequest name(@Nullable String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public @Nullable String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

  public UpdateBankAccountRequest accountType(@Nullable AccountTypeEnum accountType) {
    this.accountType = accountType;
    return this;
  }

  /**
   * Get accountType
   * @return accountType
   */
  
  @Schema(name = "accountType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accountType")
  public @Nullable AccountTypeEnum getAccountType() {
    return accountType;
  }

  public void setAccountType(@Nullable AccountTypeEnum accountType) {
    this.accountType = accountType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateBankAccountRequest updateBankAccountRequest = (UpdateBankAccountRequest) o;
    return Objects.equals(this.name, updateBankAccountRequest.name) &&
        Objects.equals(this.accountType, updateBankAccountRequest.accountType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, accountType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateBankAccountRequest {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    accountType: ").append(toIndentedString(accountType)).append("\n");
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

