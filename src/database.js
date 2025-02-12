const sqlite3 = require("sqlite3").verbose();
const defaultUsers = require("../default_users.json");

/**
 * @author xgraza
 * @since 02/12/25
 */
module.exports = class Database {
    #db

    cache = new Map();

    constructor() {
        const db = new sqlite3.Database("db.sqlite", (e) => console.error(e));
        this.#db = db;

        db.on("open", this._onopen);
        db.on("close", this._onclose);
        db.on("error", (e) => console.error(e));

        db.serialize(() => {
            db.run("CREATE TABLE IF NOT EXISTS users (username TEXT, alias TEXT, role INT)");
            for (const defaultUser of defaultUsers) {
                db.prepare("INSERT OR ABORT INTO users VALUES (?, ?, ?)",
                    [defaultUser.username, defaultUser.alias, defaultUser.role], 
                    (_, e) => {
                        if (!e) {
                            console.info(`Added default user "${defaultUser.username}" successfully`);
                            this.cache.set(defaultUser.username, { ...defaultUser });
                        }
                    });
            }
        })
    }

    /**
     * @private
     */
    _onopen() {
        console.info("Opened database")
    }

    /**
     * @private
     */
    _onclose() {
        console.warn("Database connection closed?");
    }
}
