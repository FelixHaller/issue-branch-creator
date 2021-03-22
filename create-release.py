#!/usr/bin/env python3
import re
import sys
from datetime import datetime
from subprocess import run, CalledProcessError

PROPERTIES_FILENAME = "gradle.properties"


def main():
    tag_name = datetime.utcnow().strftime("%Y.%m.%d.%H%M")
    update_plugin_version(tag_name)
    create_tag(tag_name)
    push_tags(tag_name)

    print(f"sucessfully pushed release tag {tag_name}")


def update_plugin_version(version):
    with open(PROPERTIES_FILENAME, "r") as config:
        lines = config.readlines()
    for i, line in enumerate(lines):
        if re.match("pluginVersion\\s*=", line):
            lines[i] = f"pluginVersion={version}\n"
    with open(PROPERTIES_FILENAME, "w") as config:
        config.writelines(lines)


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
