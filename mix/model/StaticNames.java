package model;

public class StaticNames
{
    private StaticNames(){}


    public static final String CLIENT_DESTINATION = "client";
    public static final String BROKER_FROM_CLIENT_DESTINATION = "broker.client";
    public static final String BROKER_FROM_BANK_DESTINATION = "broker.bank";
    public static final String ABN_AMRO_BANK_DESTINATION = "bank.abn_amro";


    public static final String LOGGER_ERROR_BUILDING_CONNECTION = "Something went wrong while building a connection";
    public static final String LOGGER_ERROR_PREPARING_TO_RECEIVE_MESSAGES = "Something went wrong while preparing to receive messages";
    public static final String LOGGER_ERROR_RECEIVING_MESSAGE = "Something went wrong while receiving a message";
    public static final String LOGGER_ERROR_SENDING_MESSAGE = "Something went wrong while sending a message";
    public static final String LOGGER_ERROR_APPLICATION_EXECUTION = "Something went wrong while executing the application";
}
