/**
 * Checks if an object has required keys
 * @param {*} obj the object to check 
 * @param  {...String} keys 
 * @returns a {@link string[]} of missing keys, or an empty array if none
 */
function getMissingParamaters(obj, ...keys) {
    const missingKeys = [];
    for (const key of keys) {
        if (!(key in obj)) {
            missingKeys.push(key);
        }
    }
    return missingKeys;
}

module.exports = { getMissingParamaters };