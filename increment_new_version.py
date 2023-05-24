import argparse
import subprocess


def get_latest_tag():
    tag_list = (
        subprocess.check_output(['git', 'rev-list', '--tags', '--max-count=1'])
        .decode('utf-8')
        .strip("\n")
    )
    return (
        subprocess.check_output(['git', 'describe', '--tags', f'{tag_list}'])
        .decode('utf-8')
        .strip("\n")[1:]
    )


def if_tag_exists(version):
    return subprocess.check_output(
        ['git', 'tag', '-l', f'v{version.strip("v")}']
    ).decode('utf-8')


def get_next_alphabet(letter):
    ascii_code = ord(letter)
    next_ascii_code = ascii_code + 1
    next_letter = chr(next_ascii_code)
    return next_letter


def get_suffix(input_version_string, is_latest_version):
    number = ""
    alphabet = ""
    for char in input_version_string:
        if char.isdigit():
            number += char
        else:
            alphabet += char

    if is_latest_version == "default":
        return str(int(number) + 1)

    return number + get_next_alphabet(alphabet) if alphabet != "" else number + 'a'


def create_new_tag(current_version, is_latest_tag, mode):
    major, minor, patch = current_version.split(".")

    if is_latest_tag == "default":
        if mode == "major":
            major = str(int(major) + 1)
            minor, patch = '0', '0'
        elif mode == "minor":
            minor = str(int(minor) + 1)
            patch = '0'
        else:
            patch = get_suffix(patch, is_latest_tag)
        new_version = ".".join([major, minor, patch])

    else:
        patch = get_suffix(patch, is_latest_tag)
        new_version = ".".join([major, minor, patch])
        tag_already_exists = if_tag_exists(new_version)
        if tag_already_exists:
            raise ValueError(f"new tag {new_version} already exists")

    return new_version


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Increment a new version for release.")
    parser.add_argument("--stage", help="int or prod", required=True)
    parser.add_argument("--based_on_version", help="tag", required=True)
    parser.add_argument("--release_type", help="major, minor or patch", required=False)
    args = parser.parse_args()

    stage = args.stage
    based_on_version = args.based_on_version
    release_type = args.release_type

    if stage == "int":
        if based_on_version == "default":
            latest_tag = get_latest_tag()
            print(f"{create_new_tag(latest_tag, based_on_version, release_type)}")
        else:
            if release_type in ("major", "minor"):
                raise ValueError(
                    "Making major or minor release based on an old version is not allowed."
                )

            tag_exists = if_tag_exists(based_on_version)
            if not tag_exists:
                raise ValueError(f"tag v{based_on_version} does not exist.")
            print(f"{create_new_tag(based_on_version, based_on_version, release_type)}")

    else:
        if based_on_version == "default":
            raise ValueError(
                "To deploy to APPROVAL or prod stage, please specify a known version that was deployed to dev before."
            )

        tag_exists = if_tag_exists(based_on_version)
        if not tag_exists:
            raise ValueError(f"tag v{based_on_version} does not exist.")
        print(f"{based_on_version}")
