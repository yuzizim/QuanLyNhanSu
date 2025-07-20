const bcrypt = require('bcryptjs');

const plainPassword = '123@@hai';
bcrypt.hash(plainPassword, 10, (err, hash) => {
    if (err) throw err;
    console.log('Password:', plainPassword);
    console.log('Bcrypt hash:', hash);
});