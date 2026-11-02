# Security Policy

## Supported versions

The latest commit on `main` is supported. Always update before reporting an
issue that is already fixed.

## Reporting a vulnerability

Please do **not** open a public GitHub issue for security reports.

1. Email the owner via the contact in their GitHub profile, **or**
2. Use GitHub's private vulnerability reporting
   (Repository → Security → Report a vulnerability).

Include: affected version/commit, a minimal reproduction, and (if possible) a
suggested fix. You can expect an initial response within 7 days.

## Known design constraints

- The expression parser has a nesting limit (150 parentheses) to prevent
  stack-overflow crashes from pathological input.
- No API key is baked into the APK. Never uncomment `GEMINI_API_KEY` in
  `.env.example` for release builds — keys shipped in an APK are extractable
  by anyone with the binary. Use a backend proxy if server-side AI is needed.
- Release builds are sign-configured via environment variables
  (`STORE_PASSWORD`, `KEY_PASSWORD`, `KEYSTORE_PATH`); never commit the
  upload keystore (`my-upload-key.jks` is gitignored).
