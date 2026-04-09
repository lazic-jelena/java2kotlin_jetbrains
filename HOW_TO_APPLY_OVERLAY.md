# How to Apply This Overlay to Your Fork

## 1) Fork the upstream Java project

Create a public fork of:

```text
https://github.com/spring-projects/spring-petclinic
```

## 2) Clone your fork locally

```bash
git clone https://github.com/YOUR_USERNAME/spring-petclinic.git
cd spring-petclinic
```

## 3) Copy overlay files into the fork root

Copy everything from this overlay package into the root of your fork.

Important files added:

- `.github/workflows/j2k-eval.yml`
- `README.md`
- `SUMMARY.md`
- `EDGE_CASE_REPORT.md`
- `HYPOTHESES.md`
- `BANANA_CAKE.md`
- `tools/`

## 4) Commit and push

```bash
git add .
git commit -m "Add static J2K evaluation pipeline and Kotlin evaluator"
git push origin main
```

## 5) Run the workflow

Go to **Actions** in GitHub and run `j2k-eval` manually if it does not start automatically.

## 6) Download the report artifact

From the workflow run, download the `j2k-report` artifact.

## 7) Paste the real metrics into checked-in docs

Update:

- `SUMMARY.md`
- optionally `EDGE_CASE_REPORT.md`

Commit and push those report updates.

