const ws = require("ws");
const db = new (require("./database"))();
const util = require("./util");

const { OP_CODES, CLOSE_CODES } = require("./codes");

// read .env
require("./envConfigReader")();
require("./logger")();

// create our websocket server
const server = new ws.Server({ 
    port: process.env.PORT,
    clientTracking: true
});

const clientTracker = new Map();

server.on("close", () => console.warn("Socket server closed"));
server.on("listening", () => console.info(`Server listening at ws://localhost:${process.env.PORT}`));
server.on("connection", (socket, request) => {
    const remoteAddress = request.socket.remoteAddress;
    console.info(`New connection at ${remoteAddress}`);
    socket.on("open", () => {
        clientTracker.set(remoteAddress, {
            chatRatelimitEnd: -1,
            location: {
                reported: false,
                x: -1,
                y: -1,
                z: -1
            }
        });
    })
    socket.on("message", (data, binary) => {
        if (binary) {
            data = parseInt(data, 2).toString(10);
        }
        const json = data.toJSON();

        const missingKeys = util.getMissingParamaters(json, "op", "d");
        if (missingKeys.length !== 0) {
            socket.close(CLOSE_CODES.INVALID_DATA, 
                `missing key(s): ${missingKeys.join(",")} in base request...`);
            return;
        }

        const op = json["op"];
        const d = json["d"];
        switch (op) {
            case OP_CODES.IDENTIFY:
                const username = d["username"];
                break;
            case OP_CODES.RECIEVE_CHAT:
                let content = d["content"];
                if (!content || content.length === 0) {
                    socket.close(CLOSE_CODES.INVALID_DATA, "no \"content\" in d");
                    return;
                }
                content = content
                    .trim()
                    .replaceAll("§", "")
                    .substring(0, 200);
                break;
            case OP_CODES.REPORT_LOCATION:
                const requiredFields = ["x", "y", "z"];

                break;
        }
    })
});