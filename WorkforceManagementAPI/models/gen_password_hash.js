const bcrypt = require('bcryptjs');

const passwords = [
  'Chinh123@',
  'John123@',
  'Jane123@',
  'Bob123@',
  '123@hai',
  '1235@haib',
  '1234@haiz'
];

passwords.forEach(plainPassword => {
  bcrypt.hash(plainPassword, 10, (err, hash) => {
    if (err) {
      console.error(`Error hashing ${plainPassword}:`, err);
      return;
    }
    console.log('Password:', plainPassword);
    console.log('Bcrypt hash:', hash);
  });
});