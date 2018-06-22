#!/usr/bin/env python

from __future__ import print_function

import fileinput
import glob
import os
import re
import sys
import string

basedir = 'src/test/java/ru/mystamps/web/tests/cases'

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


print('Scanning %s' % basedir, file=sys.stderr)

props = load_property_file('src/main/resources/ru/mystamps/i18n/ValidationMessages.properties')
constants = load_constants()

for filepath in glob.glob('%s/*.java' % basedir):
    filename = os.path.basename(filepath)
    if filename != 'WhenAnonymousUserActivateAccount.java':
        continue
    print('Processing %s' % filename, file=sys.stderr)
    inside_test_method = 0
    test_method_body = []
    inside_test_method_num = 0
    for line in fileinput.input(filepath, inplace=True):
        if string.find(line, '@Test') > 0:
            inside_test_method = 1
            inside_test_method_num += 1
            test_method_body.append(line.strip())
        else:
            if string.find(line, '{') > 0 and not string.find(line, '}') > 0:
                if inside_test_method == 1:
                    inside_test_method = 2
            elif string.find(line, '}') > 0 and not string.find(line, '{') > 0:
                if inside_test_method == 2:
                    inside_test_method = 0
                    line_indent_count = line.count('\t') + 1
                    indent = '\t' * line_indent_count
                    test_method = filter(None, ''.join(test_method_body).replace(') {', ') {;').split(';'))
                    #print('%s/// BODY: "%s"' % (indent, test_method), file=sys.stderr)
                    # TODO: print suggested commands to the robot file
                    #suggested_list = handleTestMethod(test_method)
                    #if len(suggested_list) > 0:
                    #    print('%s/// FIXME' % indent)
                    #    directive_length = max(map(lambda item: 0 if item['name'] is None else len(item['name']), suggested_list))
                    #    for suggested_command in suggested_list:
                    #        if suggested_command['name'] is None:
                    #            print('%s/// %s' % (indent, suggested_command['args']))
                    #        else:
                    #            print('%s///\t%s  %s' % (indent, suggested_command['name'].ljust(directive_length), suggested_command['args']))
                    test_method_body[:] = []
            if inside_test_method == 2:
                curline = line.strip()
                if curline != '' and not curline.startswith("//"):
                    test_method_body.append(curline)

        # skip body of the 2nd test method (the first one is usually shouldHaveStandardStructure())
        if string.find(line, '///') < 0 and inside_test_method_num != 2:
            print(line, end='')

