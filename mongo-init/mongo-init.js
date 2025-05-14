db = db.getSiblingDB('chatdb'); // crea y usa la base 'chatdb'

db.createUser({
    user: 'chatuser',
    pwd: 'chatpass',
    roles: [{ role: 'readWrite', db: 'chatdb'}]
});
