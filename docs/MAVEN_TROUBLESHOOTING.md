# Maven Troubleshooting (Windows Local Environment)

If you see errors like **`403 Forbidden` from `https://repo.maven.apache.org`**, use the steps below to diagnose and fix common local setup/network issues.

## 1) Check `~/.m2/settings.xml` for mirrors or proxies

A bad mirror/proxy configuration can redirect Maven to blocked or invalid endpoints.

- Open your Maven settings file:
  - Windows: `%USERPROFILE%\.m2\settings.xml`
- Look for `<mirrors>` and `<proxies>` sections.
- Temporarily disable custom mirror/proxy entries and retry.
- Ensure no company proxy is forcing unauthorized outbound requests.

## 2) Try another network

Sometimes the issue is network-specific (corporate firewall, ISP filtering, VPN policy).

- Disconnect VPN/proxy and test again.
- Try a different connection (for example, mobile hotspot).
- If it works on another network, your original network likely blocks or rewrites Maven Central requests.

## 3) Clear local cache for specific artifacts

A corrupted local artifact can cause repeated failures.

- Identify the failing group/artifact/version.
- Delete only the corresponding folder under local repo:
  - `%USERPROFILE%\.m2\repository\<group-path>\<artifact>\<version>`
- Example:
  - `C:\Users\<you>\.m2\repository\org\springframework\boot\spring-boot-starter\3.3.0`

Then rerun Maven so the artifact is fetched again.

## 4) Force dependency updates

Use `-U` to force Maven to re-check remote metadata and snapshots:

```bash
mvn -U clean verify
```

You can also run:

```bash
mvn -U -e -DskipTests dependency:resolve
```

## 5) Verify Maven and Java versions

Incompatible or unexpected tool versions can produce misleading repository errors.

- Check Maven version:

```bash
mvn -v
```

- Check Java home and runtime:

```powershell
echo %JAVA_HOME%
$env:JAVA_HOME
java -version
```

Confirm:
- `JAVA_HOME` points to a valid JDK.
- `mvn -v` reports the expected Java version.
- Project-required Java version matches your local JDK.

---

If the problem persists, capture full output with `-e -X` and share sanitized logs with your team:

```bash
mvn -U -e -X -DskipTests dependency:resolve
```
