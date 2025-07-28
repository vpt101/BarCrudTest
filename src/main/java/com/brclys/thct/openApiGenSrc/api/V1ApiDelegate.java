package com.brclys.thct.openApiGenSrc.api;

import com.brclys.thct.openApiGenSrc.model.*;
import jakarta.annotation.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

/**
 * A delegate to be called by the {@link V1ApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-29T12:22:03.964972322+01:00[Europe/London]", comments = "Generator version: 7.14.0")
public interface V1ApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /v1/accounts
     * Create a new bank account
     *
     * @param createBankAccountRequest Create a new bank account for the user (required)
     * @return Bank Account has been created successfully (status code 201)
     *         or Invalid details supplied (status code 400)
     *         or Access token is missing or invalid (status code 401)
     *         or The user is not allowed to access the transaction (status code 403)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#createAccount
     */
    default ResponseEntity<BankAccountResponse> createAccount(CreateBankAccountRequest createBankAccountRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"balance\" : 800.8281904610114, \"accountType\" : \"personal\", \"createdTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"currency\" : \"GBP\", \"accountNumber\" : \"accountNumber\", \"sortCode\" : \"10-10-10\", \"updatedTimestamp\" : \"2000-01-23T04:56:07.000+00:00\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"details\" : [ { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" }, { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" } ], \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /v1/accounts/{accountNumber}/transactions
     * Create a transaction
     *
     * @param accountNumber Account number of the bank account (required)
     * @param createTransactionRequest Create a new transaction (required)
     * @return Transaction has been created successfully (status code 201)
     *         or Invalid details supplied (status code 400)
     *         or Access token is missing or invalid (status code 401)
     *         or The user is not allowed to delete the bank account details (status code 403)
     *         or Bank account was not found (status code 404)
     *         or Insufficient funds to process transaction (status code 422)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#createTransaction
     */
    default ResponseEntity<TransactionResponse> createTransaction(String accountNumber,
        CreateTransactionRequest createTransactionRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"reference\" : \"reference\", \"amount\" : 800.8281904610114, \"createdTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"currency\" : \"GBP\", \"id\" : \"id\", \"type\" : \"deposit\", \"userId\" : \"userId\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"details\" : [ { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" }, { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" } ], \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /v1/users
     * Create a new user
     *
     * @param createUserRequest Create a new user (required)
     * @return User has been created successfully (status code 201)
     *         or Invalid details supplied (status code 400)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#createUser
     */
    default ResponseEntity<UserResponse> createUser(CreateUserRequest createUserRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"address\" : { \"town\" : \"town\", \"county\" : \"county\", \"postcode\" : \"postcode\", \"line3\" : \"line3\", \"line2\" : \"line2\", \"line1\" : \"line1\" }, \"phoneNumber\" : \"phoneNumber\", \"createdTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"id\" : \"id\", \"updatedTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"email\" : \"email\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * DELETE /v1/accounts/{accountNumber}
     * Delete account by account number.
     *
     * @param accountNumber Account number of the bank account (required)
     * @return The bank account has been deleted (status code 204)
     *         or The request didn&#39;t supply all the necessary data (status code 400)
     *         or Access token is missing or invalid (status code 401)
     *         or The user is not allowed to delete the bank account details (status code 403)
     *         or Bank account was not found (status code 404)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#deleteAccountByAccountNumber
     */
    default ResponseEntity<Void> deleteAccountByAccountNumber(String accountNumber) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"details\" : [ { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" }, { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" } ], \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * DELETE /v1/users/{userId}
     * Delete user by ID.
     *
     * @param userId ID of the user (required)
     * @return The user has been deleted (status code 204)
     *         or The request didn&#39;t supply all the necessary data (status code 400)
     *         or User was not found (status code 404)
     *         or Access token is missing or invalid (status code 401)
     *         or The user is not allowed to access the transaction (status code 403)
     *         or A user cannot be deleted when they are associated with a bank account (status code 409)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#deleteUserByID
     */
    default ResponseEntity<Void> deleteUserByID(String userId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"details\" : [ { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" }, { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" } ], \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /v1/accounts/{accountNumber}
     * Fetch account by account number.
     *
     * @param accountNumber Account number of the bank account (required)
     * @return The bank account details (status code 200)
     *         or The request didn&#39;t supply all the necessary data (status code 400)
     *         or The user was not authenticated (status code 401)
     *         or The user is not allowed to access the bank account details (status code 403)
     *         or Bank account was not found (status code 404)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#fetchAccountByAccountNumber
     */
    default ResponseEntity<BankAccountResponse> fetchAccountByAccountNumber(String accountNumber) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"balance\" : 800.8281904610114, \"accountType\" : \"personal\", \"createdTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"currency\" : \"GBP\", \"accountNumber\" : \"accountNumber\", \"sortCode\" : \"10-10-10\", \"updatedTimestamp\" : \"2000-01-23T04:56:07.000+00:00\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"details\" : [ { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" }, { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" } ], \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /v1/accounts/{accountNumber}/transactions/{transactionId}
     * Fetch transaction by ID.
     *
     * @param accountNumber Account number of the bank account (required)
     * @param transactionId ID of the transaction (required)
     * @return The transaction details (status code 200)
     *         or The request didn&#39;t supply all the necessary data (status code 400)
     *         or Access token is missing or invalid (status code 401)
     *         or The user is not allowed to access the transaction (status code 403)
     *         or Bank account was not found (status code 404)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#fetchAccountTransactionByID
     */
    default ResponseEntity<TransactionResponse> fetchAccountTransactionByID(String accountNumber,
        String transactionId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"reference\" : \"reference\", \"amount\" : 800.8281904610114, \"createdTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"currency\" : \"GBP\", \"id\" : \"id\", \"type\" : \"deposit\", \"userId\" : \"userId\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"details\" : [ { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" }, { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" } ], \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /v1/users/{userId}
     * Fetch user by ID.
     *
     * @param userId ID of the user (required)
     * @return The user details (status code 200)
     *         or The request didn&#39;t supply all the necessary data (status code 400)
     *         or Access token is missing or invalid (status code 401)
     *         or The user is not allowed to access the transaction (status code 403)
     *         or User was not found (status code 404)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#fetchUserByID
     */
    default ResponseEntity<UserResponse> fetchUserByID(String userId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"address\" : { \"town\" : \"town\", \"county\" : \"county\", \"postcode\" : \"postcode\", \"line3\" : \"line3\", \"line2\" : \"line2\", \"line1\" : \"line1\" }, \"phoneNumber\" : \"phoneNumber\", \"createdTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"id\" : \"id\", \"updatedTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"email\" : \"email\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"details\" : [ { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" }, { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" } ], \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /v1/accounts/{accountNumber}/transactions
     * List transactions
     *
     * @param accountNumber Account number of the bank account (required)
     * @return The list of transaction details (status code 200)
     *         or The request didn&#39;t supply all the necessary data (status code 400)
     *         or Access token is missing or invalid (status code 401)
     *         or The user is not allowed to access the transactions (status code 403)
     *         or Bank account was not found (status code 404)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#listAccountTransaction
     */
    default ResponseEntity<ListTransactionsResponse> listAccountTransaction(String accountNumber) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"transactions\" : [ { \"reference\" : \"reference\", \"amount\" : 800.8281904610114, \"createdTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"currency\" : \"GBP\", \"id\" : \"id\", \"type\" : \"deposit\", \"userId\" : \"userId\" }, { \"reference\" : \"reference\", \"amount\" : 800.8281904610114, \"createdTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"currency\" : \"GBP\", \"id\" : \"id\", \"type\" : \"deposit\", \"userId\" : \"userId\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"details\" : [ { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" }, { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" } ], \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /v1/accounts
     * List accounts
     *
     * @return The list of bank accounts (status code 200)
     *         or Access token is missing or invalid (status code 401)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#listAccounts
     */
    default ResponseEntity<ListBankAccountsResponse> listAccounts() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"accounts\" : [ { \"balance\" : 800.8281904610114, \"accountType\" : \"personal\", \"createdTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"currency\" : \"GBP\", \"accountNumber\" : \"accountNumber\", \"sortCode\" : \"10-10-10\", \"updatedTimestamp\" : \"2000-01-23T04:56:07.000+00:00\" }, { \"balance\" : 800.8281904610114, \"accountType\" : \"personal\", \"createdTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"currency\" : \"GBP\", \"accountNumber\" : \"accountNumber\", \"sortCode\" : \"10-10-10\", \"updatedTimestamp\" : \"2000-01-23T04:56:07.000+00:00\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /v1/accounts/{accountNumber}
     * Update account by account number.
     *
     * @param accountNumber Account number of the bank account (required)
     * @param updateBankAccountRequest Update bank account details for the user (required)
     * @return The updated bank account details (status code 200)
     *         or The request didn&#39;t supply all the necessary data (status code 400)
     *         or Access token is missing or invalid (status code 401)
     *         or The user is not allowed to update the bank account details (status code 403)
     *         or Bank account was not found (status code 404)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#updateAccountByAccountNumber
     */
    default ResponseEntity<BankAccountResponse> updateAccountByAccountNumber(String accountNumber,
        UpdateBankAccountRequest updateBankAccountRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"balance\" : 800.8281904610114, \"accountType\" : \"personal\", \"createdTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"currency\" : \"GBP\", \"accountNumber\" : \"accountNumber\", \"sortCode\" : \"10-10-10\", \"updatedTimestamp\" : \"2000-01-23T04:56:07.000+00:00\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"details\" : [ { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" }, { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" } ], \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PATCH /v1/users/{userId}
     * Update user by ID.
     *
     * @param userId ID of the user (required)
     * @param updateUserRequest Update user details (required)
     * @return The updated user details (status code 200)
     *         or The request didn&#39;t supply all the necessary data (status code 400)
     *         or Access token is missing or invalid (status code 401)
     *         or The user is not allowed to access the transaction (status code 403)
     *         or User was not found (status code 404)
     *         or An unexpected error occurred (status code 500)
     * @see V1Api#updateUserByID
     */
    default ResponseEntity<UserResponse> updateUserByID(String userId,
        UpdateUserRequest updateUserRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"address\" : { \"town\" : \"town\", \"county\" : \"county\", \"postcode\" : \"postcode\", \"line3\" : \"line3\", \"line2\" : \"line2\", \"line1\" : \"line1\" }, \"phoneNumber\" : \"phoneNumber\", \"createdTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"name\" : \"name\", \"id\" : \"id\", \"updatedTimestamp\" : \"2000-01-23T04:56:07.000+00:00\", \"email\" : \"email\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"details\" : [ { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" }, { \"field\" : \"field\", \"message\" : \"message\", \"type\" : \"type\" } ], \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"message\" : \"message\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
