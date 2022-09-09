# Android TOTP

Authenticator app for generating Time-based One Time Passwords on Android.

Generation is based on [RFC 6238](https://www.rfc-editor.org/rfc/rfc6238),
currently only supports SHA-1 as hash algorithm, 30 seconds time step and 6
digit output passwords. Secret values (keys) needed for the algorithm are stored
in an SQLite database encrypted by a master key. The master key is generated and
stored in a KeyStore using AndroidKeyStore provider. Currently no user
authentication is required to use the master key (I am working on adding some
sort of authentication in the future).

## Screenshots

<img src="screenshots/home_screen.png" alt="home_screen" width="360"/>