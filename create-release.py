#!/usr/bin/env python3
import sys
from datetime import datetime
from subprocess import run, CalledProcessError

PROPERTIES_FILENAME = "gradle.properties"


def main():
    tag_name = datetime.utcnow().strftime("%Y.%m.%d.%H%M")
    create_tag(tag_name)
    push_tags(tag_name)

    print(f"sucessfully pushed release tag {tag_name}")


def create_tag(tag_name):
    try:
        run(["git", "tag", tag_name], check=True)
    except CalledProcessError:
        error_exit(f"something went wrong while creating the tag '{tag_name}'. see git output above")


def push_tags(tag_name):
    try:
        run(["git", "push", "origin", tag_name], check=True)
    except CalledProcessError:
        error_exit(f"something went wrong while pushing the tag '{tag_name}' to remote. see git output above")


def error_exit(message):
    print(message, file=sys.stderr)
    sys.exit(1)


if __name__ == "__main__":
    main()
