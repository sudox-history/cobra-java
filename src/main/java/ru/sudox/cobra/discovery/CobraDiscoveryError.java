package ru.sudox.cobra.discovery;

public enum CobraDiscoveryError {
    COBRA_DISCOVERY_OK,
    COBRA_DISCOVERY_ERR_ALREADY_OPENED,
    COBRA_DISCOVERY_ERR_ALREADY_CLOSED,
    COBRA_DISCOVERY_ERR_BINDING,
    COBRA_DISCOVERY_ERR_JOINING_GROUP,
    COBRA_DISCOVERY_ERR_SENDING_FRAME,
    COBRA_DISCOVERY_ERR_NOT_CLOSED,
    COBRA_DISCOVERY_ERR_GETTING_ADDRESSES
}