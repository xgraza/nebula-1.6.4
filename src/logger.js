const { format } = require("util");
const { existsSync, createWriteStream, mkdirSync } = require("fs");
const { join } = require("path");

/**
 * @author xgraza
 * @since 02/12/25
 * @description overrides the console.log, console.warn, and console.error functions to have colored output
 */
module.exports = () => {
    const writeStream = createLogFile();
    function logOverride(type, message, optionalParams) {
        const time = new Date();
        
        //message = format(message, optionalParams);
        message = `${formatTime(time)} [${type}] ${message}\n`;
        writeStream.write(message);
        process.stdout.write(message);
    }
    console.log = function (message, ...optionalParams) {
        logOverride("LOG", message, optionalParams);
    }
    console.info = function (message, ...optionalParams) {
        logOverride("INFO", message, optionalParams);
    }
    console.warn = function (message, ...optionalParams) {
        logOverride("WARN", message, optionalParams);
    }
    console.error = function (message, ...optionalParams) {
        logOverride("ERROR", message, optionalParams);
    }
    console.debug = function (message, ...optionalParams) {
        logOverride("DEBUG", message, optionalParams);
    }
}

/**
 * Creates a log file (and its parent directory if it does not already exist)
 * @returns a {@link WriteStream} to the log file
 */
function createLogFile() {
    const logs = join(__dirname, "..", "logs");
    if (!existsSync(logs)) {
        mkdirSync(logs);
    }
    return createWriteStream(join(logs, `log_${Date.now()}.txt`));
}

/**
 * Formats time to a readable string
 * @param {Date} input the date input 
 */
function formatTime(input) {
    return `${input.toLocaleDateString()} ${input.toLocaleTimeString("en-us", { hour12: false })}`;
}