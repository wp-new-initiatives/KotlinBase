if [[ ! -x "$(command -v ktlint)" ]]; then
  brew install ktlint
fi
git config core.hooksPath scripts/githooks
