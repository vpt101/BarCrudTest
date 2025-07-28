package com.brclys.thct.openApiGenSrc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * BankAccountResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-29T12:22:03.964972322+01:00[Europe/London]", comments = "Generator version: 7.14.0")
public class BankAccountResponse {

  private String accountNumber;

  /**
   * Gets or Sets sortCode
   */
  public enum SortCodeEnum {
    _10_10_10("10-10-10");

    private final String value;

    SortCodeEnum(String value) {
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
    public static SortCodeEnum fromValue(String value) {
      for (SortCodeEnum b : SortCodeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private SortCodeEnum sortCode;

  private String name;

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

  private AccountTypeEnum accountType;

  private Double balance;

  /**
   * Gets or Sets currency
   */
  public enum CurrencyEnum {
    GBP("GBP");

    private final String value;

    CurrencyEnum(String value) {
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
    public static CurrencyEnum fromValue(String value) {
      for (CurrencyEnum b : CurrencyEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private CurrencyEnum currency;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdTimestamp;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedTimestamp;

  public BankAccountResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BankAccountResponse(String accountNumber, SortCodeEnum sortCode, String name, AccountTypeEnum accountType, Double balance, CurrencyEnum currency, OffsetDateTime createdTimestamp, OffsetDateTime updatedTimestamp) {
    this.accountNumber = accountNumber;
    this.sortCode = sortCode;
    this.name = name;
    this.accountType = accountType;
    this.balance = balance;
    this.currency = currency;
    this.createdTimestamp = createdTimestamp;
    this.updatedTimestamp = updatedTimestamp;
  }

  public BankAccountResponse accountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
    return this;
  }

  /**
   * Get accountNumber
   * @return accountNumber
   */
  @NotNull 
  @Schema(name = "accountNumber", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("accountNumber")
  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public BankAccountResponse sortCode(SortCodeEnum sortCode) {
    this.sortCode = sortCode;
    return this;
  }

  /**
   * Get sortCode
   * @return sortCode
   */
  @NotNull 
  @Schema(name = "sortCode", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sortCode")
  public SortCodeEnum getSortCode() {
    return sortCode;
  }

  public void setSortCode(SortCodeEnum sortCode) {
    this.sortCode = sortCode;
  }

  public BankAccountResponse name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  @NotNull 
  @Schema(name = "name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BankAccountResponse accountType(AccountTypeEnum accountType) {
    this.accountType = accountType;
    return this;
  }

  /**
   * Get accountType
   * @return accountType
   */
  @NotNull 
  @Schema(name = "accountType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("accountType")
  public AccountTypeEnum getAccountType() {
    return accountType;
  }

  public void setAccountType(AccountTypeEnum accountType) {
    this.accountType = accountType;
  }

  public BankAccountResponse balance(Double balance) {
    this.balance = balance;
    return this;
  }

  /**
   * Currency amount with up to two decimal places
   * minimum: 0.0
   * maximum: 10000.0
   * @return balance
   */
  @NotNull @DecimalMin("0.0") @DecimalMax("10000.0") 
  @Schema(name = "balance", description = "Currency amount with up to two decimal places", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("balance")
  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  public BankAccountResponse currency(CurrencyEnum currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
   */
  @NotNull 
  @Schema(name = "currency", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("currency")
  public CurrencyEnum getCurrency() {
    return currency;
  }

  public void setCurrency(CurrencyEnum currency) {
    this.currency = currency;
  }

  public BankAccountResponse createdTimestamp(OffsetDateTime createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
    return this;
  }

  /**
   * Get createdTimestamp
   * @return createdTimestamp
   */
  @NotNull @Valid 
  @Schema(name = "createdTimestamp", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdTimestamp")
  public OffsetDateTime getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(OffsetDateTime createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

  public BankAccountResponse updatedTimestamp(OffsetDateTime updatedTimestamp) {
    this.updatedTimestamp = updatedTimestamp;
    return this;
  }

  /**
   * Get updatedTimestamp
   * @return updatedTimestamp
   */
  @NotNull @Valid 
  @Schema(name = "updatedTimestamp", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("updatedTimestamp")
  public OffsetDateTime getUpdatedTimestamp() {
    return updatedTimestamp;
  }

  public void setUpdatedTimestamp(OffsetDateTime updatedTimestamp) {
    this.updatedTimestamp = updatedTimestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BankAccountResponse bankAccountResponse = (BankAccountResponse) o;
    return Objects.equals(this.accountNumber, bankAccountResponse.accountNumber) &&
        Objects.equals(this.sortCode, bankAccountResponse.sortCode) &&
        Objects.equals(this.name, bankAccountResponse.name) &&
        Objects.equals(this.accountType, bankAccountResponse.accountType) &&
        Objects.equals(this.balance, bankAccountResponse.balance) &&
        Objects.equals(this.currency, bankAccountResponse.currency) &&
        Objects.equals(this.createdTimestamp, bankAccountResponse.createdTimestamp) &&
        Objects.equals(this.updatedTimestamp, bankAccountResponse.updatedTimestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountNumber, sortCode, name, accountType, balance, currency, createdTimestamp, updatedTimestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BankAccountResponse {\n");
    sb.append("    accountNumber: ").append(toIndentedString(accountNumber)).append("\n");
    sb.append("    sortCode: ").append(toIndentedString(sortCode)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    accountType: ").append(toIndentedString(accountType)).append("\n");
    sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    createdTimestamp: ").append(toIndentedString(createdTimestamp)).append("\n");
    sb.append("    updatedTimestamp: ").append(toIndentedString(updatedTimestamp)).append("\n");
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

