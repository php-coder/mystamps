#!/usr/bin/env python

from __future__ import print_function

import fileinput
import glob
import os
import re
import sys
import string

basedir  = 'src/test/java/ru/mystamps/web/tests/cases'
robotdir = 'src/test/robotframework'

def load_property_file(filepath):
    props = {}
    for line in fileinput.input(filepath):
        res = string.split(line, ' = ', 1)
        if len(res) == 2:
            props[res[0]] = res[1].rstrip('\n')
    return props

def load_constants():
    consts = {
        'LOGIN_MIN_LENGTH': '2',
        'LOGIN_MAX_LENGTH': '15',
        'PASSWORD_MIN_LENGTH': '4',
        'PASSWORD_MAX_LENGTH': '72',
        'ACT_KEY_LENGTH': '10',
        'NAME_MAX_LENGTH': '100'
    }
    return consts


def string_or_variable(value):
    if value.startswith('"'):
        if value != '""':
            return value.strip('"')
        return '<EMPTY-STRING>'
    if value.startswith('tr('):
        return value
    return '$' + value

def handleTestMethod(lines):
    suggested_list = []
    for line in lines:
        found = False
        res = handleMethodName(line)
        if res != None:
            if res['name'] == None and res['args'] == 'shouldHaveStandardStructure':
                # ignore this method
                return []
            else:
                found = True
                suggested_list.append(res)
        if not found:
            res = handlePageShouldNotContainElement(line)
            if res != None:
                found = True
                suggested_list.append(res)
        if not found:
            res = handleHasError(line)
            if res != None:
                found = True
                suggested_list.append(res)
        if not found:
            res = handleHasValue(line)
            if res != None:
                found = True
                suggested_list.append(res)
        if not found:
            res = handleActivateAccount(line)
            if res != None:
                found = True
                suggested_list.extend(res)
        if not found:
            res = handleLocationShouldBe(line)
            if res != None:
                found = True
                suggested_list.append(res)
        if not found:
            res = handleGoTo(line)
            if res != None:
                found = True
                suggested_list.append(res)
        if not found:
            res = handleLinkWithLabelExist(line)
            if res != None:
                found = True
                suggested_list.append(res)
    return suggested_list

def handleMethodName(line):
    res = re.search(r'public void ([^(]+)\(', line);
    if res == None:
        return None
    value = res.group(1)
    if value == 'shouldHaveStandardStructure':
        # will be handled by caller
        return { 'name': None, 'args': value }
    value = value.replace('URL', 'Url')
    return { 'name': None, 'args': re.sub('([A-Z])', r' \1', value).lower().capitalize() }

def handleGoTo(line):
    res = re.search(r'page\.open\(([^)]*)\)', line);
    if res == None:
        return None
    return { 'name': 'Go To', 'args': string_or_variable(res.group(1)) }

def handlePageShouldNotContainElement(line):
    res = re.search(r'assertThat\(page\)\.field\("([^"]+)"\)\.hasNoError\(\)', line)
    if res == None:
        return None
    return { 'name': 'Page Should Not Contain Element', 'args': 'id=%s.errors' % res.group(1) }

def handleHasValue(line):
    res = re.search(r'assertThat\(page\)\.field\("([^"]+)"\)\.hasValue\((["]?[^")]+["]?)\)', line)
    if res == None:
        return None
    value = string_or_variable(res.group(2))
    return { 'name': 'Textfield Value Should Be', 'args': 'id=%s   %s' % (res.group(1), value) }

def handleHasError(line):
    res = re.search(r'assertThat\(page\)\.field\("([^"]+)"\)\.hasError\(([^;]+)\)', line)
    if res == None:
        return None
    locator = res.group(1)
    value = string_or_variable(res.group(2))
    if value.startswith('tr('):
        res = re.search(r'tr\("([^"]+)"\)', value)
        if res != None and res.group(1) in props:
            value = props[res.group(1)]
        res = re.search(r'tr\("([^"]+)", ([^)]+)\)', value)
        if res != None and res.group(1) in props and res.group(2) in constants:
            fmt = props[res.group(1)]
            const_val = constants[res.group(2)]
            value = re.sub(r'{[^}]+}', const_val, fmt)
    return { 'name': 'Element Text Should Be', 'args': 'id=%s.errors  %s' % (locator, value) }

def handleActivateAccount(line):
    res = re.search(r'page\.activateAccount\(("?[^,"]+"?), ?("?[^,"]*"?), ?("?[^,"]+"?), ?("?[^,"]+"?), ?("?[^)"]+"?)\)', line)
    if res == None:
        return None
    ret = []
    if res.group(1) != 'null':
        value = string_or_variable(res.group(1))
        ret.append({ 'name': 'Input Text', 'args': 'id=login  %s' % value.replace(' ', '${SPACE}') })
    if res.group(2) != 'null':
        value = string_or_variable(res.group(2))
        ret.append({ 'name': 'Input Text', 'args': 'id=name  %s' % value.replace(' ', '${SPACE}') })
    if res.group(3) != 'null':
        value = string_or_variable(res.group(3))
        ret.append({ 'name': 'Input Text', 'args': 'id=password  %s' % value })
    if res.group(4) != 'null':
        value = string_or_variable(res.group(4))
        ret.append({ 'name': 'Input Text', 'args': 'id=passwordConfirmation  %s' % value })
    if res.group(5) != 'null':
        value = string_or_variable(res.group(5))
        ret.append({ 'name': 'Input Text', 'args': 'id=activationKey  %s' % value })
    ret.append({ 'name': 'Submit Form', 'args': 'id=activate-account-form'})
    return ret

def handleLocationShouldBe(line):
    res = re.search(r'assertThat\(page\.getCurrentUrl\(\)\).isEqualTo\(([^)]+)\)', line)
    if res == None:
        return None
    value = res.group(1)
    value = value.replace('Url.AUTHENTICATION_PAGE', '${SITE_URL}/account/auth')
    value = value.replace('Url.ACTIVATE_ACCOUNT_PAGE', '${SITE_URL}/account/activate')
    return { 'name': 'Location Should Be', 'args': value }

def handleLinkWithLabelExist(line):
    res = re.search(r'assertThat\(page\.linkWithLabelExists\(tr\(\"([^"]+)', line)
    if res == None:
        return None
    link = props[res.group(1)]
    return { 'name': 'Page Should Contain Link', 'args': 'link=%s' % link }

def write_robot_test(filename, test_method):
    suggested_list = handleTestMethod(test_method)
    if len(suggested_list) <= 1:
        return False
    if not filename in javaFiles2robot:
        print('ERROR: could not find a corresponding robot file for %s' % filename, file=sys.stderr)
        sys.exit(1)
    robotFilename = javaFiles2robot[filename]
    robotFile = os.path.join(robotdir, robotFilename)
    print('Updating %s' % robotFile, file=sys.stderr)
    inside_test_cases_block = False
    append_our_case_now = False
    for line in fileinput.FileInput(robotFile, inplace=True):
        if line.startswith('*** Test Cases ***'):
            inside_test_cases_block = True
            pass
        elif line.startswith('*** '):
            if inside_test_cases_block:
                inside_test_cases_block = False
                append_our_case_now = True
            pass
        if append_our_case_now:
            append_our_case_now = False
            directive_length = max(map(lambda item: 0 if item['name'] is None else len(item['name']), suggested_list))
            for suggested_command in suggested_list:
                if suggested_command['name'] is None:
                    print('%s' % suggested_command['args'])
                else:
                    print('\t%s  %s' % (suggested_command['name'].ljust(directive_length), suggested_command['args']))
            print('')
        print(line, end='')
    return True


print('Scanning %s' % basedir, file=sys.stderr)

props = load_property_file('src/main/resources/ru/mystamps/i18n/ValidationMessages.properties')
props.update(load_property_file('src/main/resources/ru/mystamps/i18n/Messages.properties'))

constants = load_constants()

first_test_method_proccessed = False
processed_java_method = ''
processed_java_file = ''
processed_robot_file = ''

javaFiles2robot = {
    'WhenAdminAtIndexPage.java' : 'site/misc-admin.robot',
    'WhenUserAtIndexPage.java'  : 'site/misc-user.robot',
}

for filepath in sorted(glob.glob('%s/*.java' % basedir)):
    filename = os.path.basename(filepath)
    if first_test_method_proccessed:
        break
    inside_test_method = 0
    test_method_body = []
    inside_test_method_num = 0
    for line in fileinput.FileInput(filepath, inplace=True):
        if string.find(line, '@Test') > 0:
            inside_test_method = 1
            inside_test_method_num += 1
            # the 2nd because the first test is usually shouldHaveStandardStructure()
            if inside_test_method_num == 2:
                first_test_method_proccessed = True
            test_method_body.append(line.strip())
        else:
            if string.find(line, '{') > 0 and not string.find(line, '}') > 0:
                if inside_test_method == 1:
                    inside_test_method = 2
            elif string.find(line, '}') > 0 and not string.find(line, '{') > 0:
                inside_test_method = 0
                if inside_test_method == 2:
                    line_indent_count = line.count('\t') + 1
                    indent = '\t' * line_indent_count
                    test_method = filter(None, ''.join(test_method_body).replace(') {', ') {;').split(';'))
                    #print('%s/// BODY: "%s"' % (indent, test_method), file=sys.stderr)
                    # the 2nd because the first test is usually shouldHaveStandardStructure()
                    if inside_test_method_num == 2:
                        if write_robot_test(filename, test_method):
                            processed_java_file = filename
                            processed_robot_file = javaFiles2robot[filename]
                            processed_java_method = 'XXX'
                            res = re.search(r'void ([^\(]+)\(', test_method[0])
                            if res != None:
                                processed_java_method = res.group(1)
                    test_method_body[:] = []
            if inside_test_method == 2:
                curline = line.strip()
                if curline != '' and not curline.startswith("//"):
                    test_method_body.append(curline)

        # skip body of the 2nd test method (the first one is usually shouldHaveStandardStructure())
        if string.find(line, '///') < 0 and inside_test_method_num != 2:
            print(line, end='')

    if first_test_method_proccessed == True:
        print('Processed %s' % filename, file=sys.stderr)
        print("\nUse the following command to commit results:\n\ngit commit -m '%s.%s(): port to Robot Framework.\n\nAddressed to #530\n\nNo functional changes.' %s %s" % (processed_java_file.rstrip('.java'), processed_java_method, os.path.join(basedir, processed_java_file), os.path.join(robotdir, processed_robot_file)), file=sys.stderr)
    else:
        print('Skipped %s' % filename, file=sys.stderr)
