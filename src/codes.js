const OP_CODES = {
    // from client
    IDENTIFY: 0,
    RECIEVE_CHAT: 1,
    REPORT_LOCATION: 2,
    REQUEST_ONLINE_USERS: 3,

    // to client
    ONLINE_USERS: 11,
    SEND_CHAT: 12,
};

const CLOSE_CODES = {
    INVALID_DATA: 4001
}

module.exports = { OP_CODES, CLOSE_CODES };