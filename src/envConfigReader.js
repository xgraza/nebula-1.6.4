const { readFileSync, existsSync } = require("fs");
const { join } = require("path");

/**
 * @author xgraza
 * @since 02/12/25
 * @description Reads the .env file in the main project directory
 */
module.exports = () => {
    const path = join(__dirname, "..", ".env");
    if (!existsSync(path)) {
        throw new Error(".env not found in project directory");
    }
    const content = readFileSync(path).toString();
    for (const line of content.split("\n")) {
        // ignore comments
        if (line.startsWith("#")) {
            continue;
        }
        const [k, v] = line.split("=");
        process.env[k.replaceAll("\s*", "_")] = v;
    }
}