# UnitWise Git Hooks

This directory contains the Git hooks used to enforce code quality and security in this project:

- `commit-msg`: Ensures that all commit messages follow the [Conventional Commits](https://www.conventionalcommits.org/) specification (e.g., `feat: ...`, `fix: ...`).
- `pre-commit`: Scans staged files before a commit is created to prevent checking in passwords, secret keys, or sensitive binary files (`.jks`, `.apk`, etc.).
- `pre-push`: Runs a local sanity build (`assembleDebug`) before pushing to a remote repository, ensuring that you don't push broken code.

## 🚀 How to activate them manually?

If you just cloned the repository or for some reason the hooks aren't running, you need to tell Git to use this directory for its hooks.

Open a terminal at the root of the project and run:

```bash
git config core.hooksPath .githooks
```

*(On macOS/Linux, make sure the hook files have execution permissions by running: `chmod +x .githooks/*`)*

And that's it! Git will automatically execute these scripts every time you make a commit or a push.
