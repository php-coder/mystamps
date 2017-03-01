# This file is written on Ruby language.
# Here is a quick Ruby overview: http://danger.systems/guides/a_quick_ruby_overview.html
# See also Danger specific methods: http://danger.systems/reference.html

# We we'll use nokogiri for parsing XML
# Here is a good introduction: https://blog.engineyard.com/2010/getting-started-with-nokogiri
# See also a cheat sheet: https://github.com/sparklemotion/nokogiri/wiki/Cheat-sheet
require 'nokogiri'

pwd = Dir.pwd + '/'

def print_errors_summary(program, errors, link = '')
	return if errors == 0
	
	msg = ''
	if errors == 1
		msg = "#{program} reported about #{errors} error. Please, fix it."
	elsif errors > 0
		msg = "#{program} reported about #{errors} errors. Please, fix them."
	end
	
	unless link.empty?
		msg << " See also: <a href=\"#{link}\">#{link}</a>"
	end
	
	message(msg)
end

# Handle `mvn checkstyle:check` results
#
# Example:
# <file name="/home/coder/mystamps/src/main/java/ru/mystamps/web/controller/CategoryController.java">
#   <error line="64" severity="warning" message="Line is longer than 100 characters (found 131)." source="com.puppycrawl.tools.checkstyle.checks.sizes.LineLengthCheck"/>
# </file>
#
cs_report = 'target/checkstyle-result.xml'
unless File.file?(cs_report)
	warn("Couldn't find #{cs_report}. maven-checkstyle-plugin result is unknown")
else
	errors_count = 0
	doc = Nokogiri::XML(File.open(cs_report))
	doc.xpath('//error').each do |node|
		errors_count += 1
		line = node['line']
		msg  = node['message'].sub(/\.$/, '')
		file = node.parent['name'].sub(pwd, '')
		file = github.html_link("#{file}#L#{line}")
		fail("maven-checkstyle-plugin error in #{file}:\n#{msg}")
	end
	print_errors_summary 'maven-checkstyle-plugin', errors_count, 'https://github.com/php-coder/mystamps/wiki/checkstyle'
end

# Handle `mvn pmd:check` results
#
# Example:
# <file name="/home/coder/mystamps/src/main/java/ru/mystamps/web/dao/impl/JdbcSeriesDao.java">
#   <violation beginline="46" endline="361" begincolumn="49" endcolumn="1" rule="TooManyMethods" ruleset="Code Size" package="ru.mystamps.web.dao.impl" class="JdbcSeriesDao" externalInfoUrl="https://pmd.github.io/pmd-5.5.2/pmd-java/rules/java/codesize.html#TooManyMethods" priority="3">
#     This class has too many methods, consider refactoring it.
#   </violation>
# </file>
#
pmd_report = 'target/pmd.xml'
unless File.file?(pmd_report)
	warn("Couldn't find #{pmd_report}. maven-pmd-plugin result is unknown")
else
	errors_count = 0
	doc = Nokogiri::XML(File.open(pmd_report))
	doc.xpath('//violation').each do |node|
		errors_count += 1
		from_line = node['beginline']
		to_line = node['endline']
		line = "#L#{from_line}"
		if to_line.to_i > from_line.to_i
			line << '-L' << to_line
		end
		msg  = node.text.lstrip.sub(/\.$/, '')
		file = node.parent['name'].sub(pwd, '')
		file = github.html_link("#{file}#{line}")
		fail("maven-pmd-plugin error in #{file}:\n#{msg}")
	end
	print_errors_summary 'maven-pmd-plugin', errors_count, 'https://github.com/php-coder/mystamps/wiki/pmd-cpd'
end

# Handle `mvn codenarc:codenarc` results
#
# Example:
# <CodeNarc url="http://www.codenarc.org" version="0.25.2">
#   <Project title="&quot;My Stamps&quot;">
#     <SourceDirectory>/home/coder/mystamps/src/test/groovy</SourceDirectory>
#   </Project>
#   <Package path="ru/mystamps/web/service" totalFiles="14" filesWithViolations="2" priority1="0" priority2="0" priority3="4">
#     <File name="CollectionServiceImplTest.groovy">
#       <Violation ruleName="UnnecessaryGString" priority="3" lineNumber="99">
#         <SourceLine><![CDATA[service.createCollection(123, "any-login")]]></SourceLine>
#         <Message><![CDATA[The String 'any-login' can be wrapped in single quotes instead of double quotes]]></Message>
#       </Violation>
#     </File>
#   </Package>
# </CodeNarc>
#
codenarc_report = 'target/CodeNarc.xml'
unless File.file?(codenarc_report)
	warn("Couldn't find #{codenarc_report}. codenarc-maven-plugin result is unknown")
else
	errors_count = 0
	doc = Nokogiri::XML(File.open(codenarc_report))
	root_dir = doc.xpath('//SourceDirectory').first.text.sub(pwd, '')
	doc.xpath('//Violation').each do |node|
		errors_count += 1
		path = node.parent.parent['path']
		line = node['lineNumber']
		msg  = node.xpath('./Message').first.text
		file = node.parent['name']
		file = github.html_link("#{root_dir}/#{path}/#{file}#L#{line}")
		fail("codenarc-maven-plugin error in #{file}:\n#{msg}")
	end
	print_errors_summary 'codenarc-maven-plugin', errors_count, 'https://github.com/php-coder/mystamps/wiki/codenarc'
end

# Handle `mvn license:check` output
#
# Example:
# [INFO] --- license-maven-plugin:3.0:check (default-cli) @ mystamps ---
# [INFO] Checking licenses...
# [WARNING] Missing header in: /home/coder/mystamps/src/main/java/ru/mystamps/web/Db.java
# [INFO] ------------------------------------------------------------------------
# [INFO] BUILD FAILURE
# [INFO] ------------------------------------------------------------------------
#
license_output = 'license.log'
unless File.file?(license_output)
	warn("Couldn't find #{license_output}. license-maven-plugin result is unknown")
else
	errors = []
	File.readlines(license_output)
		.select { |line| line.start_with? '[WARNING]' }
		.each do |line|
			line.sub!('[WARNING] ', '')
			line.sub!(pwd, '')
			if line =~ /Missing header in: .*/
				parsed = line.match(/(?<msg>Missing header in: )(?<file>.*)/)
				msg    = parsed['msg']
				file   = parsed['file']
				file   = github.html_link("#{file}#L1")
				line = "#{msg}#{file}"
			end
			errors << line
		end
	
	unless errors.empty?
		link = 'https://github.com/php-coder/mystamps/wiki/check-license-header'
		errors_cnt = errors.size()
		error_msgs = errors.join("</li>\n<li>")
		if errors_cnt == 1
			fail("license-maven-plugin reported about #{errors_cnt} error:\n"\
				"<ul><li>#{error_msgs}</li></ul>\n"\
				"Please, fix it by executing `mvn license:format`\n"\
				"See also: <a href=\"#{link}\">#{link}</a>")
		elsif errors_cnt > 1
			fail("license-maven-plugin reported about #{errors_cnt} errors:\n"\
				"<ul><li>#{error_msgs}</li></ul>\n"\
				"Please, fix them by executing `mvn license:format`\n"\
				"See also: <a href=\"#{link}\">#{link}</a>")
		end
	end
end

# Handle `mvn sortpom:verify` output
#
# Example:
# [INFO] --- sortpom-maven-plugin:2.5.0:verify (default-cli) @ mystamps ---
# [INFO] Verifying file /home/coder/mystamps/pom.xml
# [ERROR] The xml element <groupId>com.github.heneke.thymeleaf</groupId> should be placed before <groupId>javax.validation</groupId>
# [ERROR] The file /home/coder/mystamps/pom.xml is not sorted
# [INFO] ------------------------------------------------------------------------
# [INFO] BUILD FAILURE
# [INFO] ------------------------------------------------------------------------
#
sortpom_output = 'pom.log'
unless File.file?(sortpom_output)
	warn("Couldn't find #{sortpom_output}. sortpom-maven-plugin result is unknown")
else
	errors = []
	File.readlines(sortpom_output).each do |line|
		# don't process lines after this message
		if line.start_with? '[INFO] BUILD FAILURE'
			break
		end
		
		# ignore non-error messages
		unless line.start_with? '[ERROR]'
			next
		end
		
		line.sub!('[ERROR] ', '')
		errors << line.rstrip
	end
	
	unless errors.empty?
		link = 'https://github.com/php-coder/mystamps/wiki/sortpom'
		errors_cnt = errors.size()
		error_msgs = errors.join("</li>\n<li>")
		if errors_cnt == 1
			fail("sortpom-maven-plugin reported about #{errors_cnt} error:\n"\
				"<ul><li>#{error_msgs}</li></ul>\n"\
				"Please, fix it by executing `mvn sortpom:sort`\n"\
				"See also: <a href=\"#{link}\">#{link}</a>")
		elsif errors_cnt > 1
			fail("sortpom-maven-plugin reported about #{errors_cnt} errors:\n"\
				"<ul><li>#{error_msgs}</li></ul>\n"\
				"Please, fix them by executing `mvn sortpom:sort`\n"\
				"See also: <a href=\"#{link}\">#{link}</a>")
		end
	end
end

# Handle `bootlint` output
#
# Example:
# src/main/webapp/WEB-INF/views/series/info.html:123:12 E013 Only columns (`.col-*-*`) may be children of `.row`s
# src/main/webapp/WEB-INF/views/site/events.html:197:7 E013 Only columns (`.col-*-*`) may be children of `.row`s
#
# For details, look up the lint problem IDs in the Bootlint wiki:
# https://github.com/twbs/bootlint/wiki
# 3 lint error(s) found across 20 file(s).
#
bootlint_output = 'bootlint.log'
unless File.file?(bootlint_output)
	warn("Couldn't find #{bootlint_output}. Result of bootlint is unknown")
else
	errors_count = 0
	File.readlines(bootlint_output).each do |line|
		if line !~ /:\d+:\d+/
			next
		end
		
		errors_count += 1
		
		parsed = line.match(/^(?<file>[^:]+):(?<line>\d+):\d+ (?<code>[^ ]+) (?<msg>.*)/)
		msg    = parsed['msg']
		lineno = parsed['line']
		file   = parsed['file']
		code   = parsed['code']
		file   = github.html_link("#{file}#L#{lineno}")
		fail("bootlint error in #{file}:\n#{code}: #{msg}. ([Details](https://github.com/twbs/bootlint/wiki/#{code}))")
	end
	# TODO: add link to wiki page (#316)
	print_errors_summary 'bootlint', errors_count
end

# Handle `rflint` output
#
# Example:
# + src/test/robotframework/series/creation/logic.robot
# E: 35, 0: Too many steps (34) in test case (TooManyTestSteps)
#
rflint_output = 'rflint.log'
unless File.file?(rflint_output)
	warn("Couldn't find #{rflint_output}. Result of rflint is unknown")
else
	errors_count = 0
	current_file = ''
	File.readlines(rflint_output).each do |line|
		if line.start_with? '+ '
			current_file = line.sub(/^\+ /, '').rstrip
			next
		end
		
		errors_count += 1
		
		parsed = line.match(/[A-Z]: (?<line>\d+), [^:]+: (?<msg>.*)/)
		msg    = parsed['msg'].sub(/ \(\w+\)$/, '')
		lineno = parsed['line']
		file   = github.html_link("#{current_file}#L#{lineno}")
		fail("rflint error in #{file}:\n#{msg}")
	end
	print_errors_summary 'rflint', errors_count, 'https://github.com/php-coder/mystamps/wiki/rflint'
end

# Handle `mvn enforcer:enforce` results
#
# Example:
# [INFO] --- maven-enforcer-plugin:1.4.1:enforce (default-cli) @ mystamps ---
# [WARNING] Rule 0: org.apache.maven.plugins.enforcer.BannedDependencies failed with message:
# Found Banned Dependency: junit:junit:jar:4.12
# Use 'mvn dependency:tree' to locate the source of the banned dependencies.
# [INFO] ------------------------------------------------------------------------
# [INFO] BUILD FAILURE
# [INFO] ------------------------------------------------------------------------
#
enforcer_output = 'enforcer.log'
unless File.file?(enforcer_output)
	warn("Couldn't find #{enforcer_output}. maven-enforcer-plugin result is unknown")
else
	errors = []
	plugin_output_started = false
	File.readlines(enforcer_output).each do |line|
		# We're interesting in everything between
		#     [INFO] --- maven-enforcer-plugin:1.4.1:enforce (default-cli) @ mystamps ---
		# and
		#     [INFO] ------------------------------------------------------------------------
		# lines
		
		if line.start_with? '[INFO] --- maven-enforcer-plugin:'
			plugin_output_started = true
			next
		end
		
		unless plugin_output_started
			next
		end
		
		if line.start_with? '[INFO] -----'
			break
		end
		
		if line.start_with? '[INFO] Download'
			next
		end
		
		line.sub!('[WARNING] ', '')
		errors << line.rstrip
	end
	
	unless errors.empty?
		error_msgs = errors.join("\n")
		fail("maven-enforcer-plugin reported about errors. Please, fix them. "\
			"Here is its output:\n```\n#{error_msgs}\n```")
	end
end

# Handle `mvn org.apache.maven.plugins:maven-compiler-plugin:compile` output
#
# Example:
# [INFO] --- maven-compiler-plugin:3.6.1:compile (default-compile) @ mystamps ---
# [INFO] Changes detected - recompiling the module!
# [INFO] Compiling 206 source files to /home/coder/mystamps/target/classes
# [INFO] -------------------------------------------------------------
# [ERROR] COMPILATION ERROR :
# [INFO] -------------------------------------------------------------
# [ERROR] /home/coder/mystamps/src/main/java/ru/mystamps/web/service/CollectionService.java:[31,32] cannot find symbol
#   symbol:   class Date
#   location: interface ru.mystamps.web.service.CollectionService
# [INFO] 1 error
# [INFO] -------------------------------------------------------------
# [INFO] ------------------------------------------------------------------------
# [INFO] BUILD FAILURE
# [INFO] ------------------------------------------------------------------------
#
# We're parsing file with `mvn test` output because compilation occurs before executing tests.
test_output = 'test.log'
unless File.file?(test_output)
	warn("Couldn't find #{test_output}. Result of running unit tests is unknown")
else
	errors = []
	plugin_output_started = false
	errors_detected = false
	File.readlines(test_output).each do |line|
		# We're interesting in everything between
		#     [INFO] --- maven-compiler-plugin:3.6.1:compile (default-compile) @ mystamps ---
		# and
		#     [INFO] --- gmaven-plugin:1.4:compile (default) @ mystamps ---
		# or
		#     [INFO] BUILD FAILURE
		
		if line.start_with? '[INFO] --- maven-compiler-plugin:'
			plugin_output_started = true
			next
		end
		
		unless plugin_output_started
			next
		end
		
		if line.start_with? '[INFO] Download'
			next
		end
		
		# next plugin started its execution => no errors encountered, stop processing
		if line.start_with? '[INFO] --- '
			break
		end
		
		# build failed => error output was collected, stop processing
		if line =~ /BUILD FAILURE/
			break
		end
		
		errors << line.rstrip
	end
	
	unless errors.empty?
		if errors.last.start_with? '[INFO] -----'
			errors.pop # remove last useless line
		end
		error_msgs = errors.join("\n")
		fail("maven-compile-plugin has failed. Please, fix compilation errors. "\
			"Here is its output:\n```\n#{error_msgs}\n```")
	end
end

# Handle `mvn findbugs:check` results
#
# Example:
# <BugCollection sequence="0" release="" analysisTimestamp="1489272156067" version="3.0.1" # timestamp="1489272147000">
#   <Project projectName="My Stamps">
#     <SrcDir>/home/coder/mystamps/src/main/java</SrcDir>
#   </Project>
#   <BugInstance instanceOccurrenceNum="0" instanceHash="70aca951e7fd81233fcdb6d19dc38a90" rank="17" abbrev="Eq" category="STYLE" priority="2" type="EQ_DOESNT_OVERRIDE_EQUALS" instanceOccurrenceMax="0">
#     <LongMessage>ru.mystamps.web.support.spring.security.CustomUserDetails doesn't override org.springframework.security.core.userdetails.User.equals(Object)</LongMessage>
#     <Class classname="ru.mystamps.web.support.spring.security.CustomUserDetails" primary="true">
#       <SourceLine classname="ru.mystamps.web.support.spring.security.CustomUserDetails" start="31" end="45" sourcepath="ru/mystamps/web/support/spring/security/CustomUserDetails.java" sourcefile="CustomUserDetails.java">
#       </SourceLine>
#     </Class>
#   </BugInstance>
# </BugCollection>
#
findbugs_report = 'target/findbugsXml.xml'
unless File.file?(findbugs_report)
	warn("Couldn't find #{findbugs_report}. findbugs-maven-plugin result is unknown")
else
	errors_count = 0
	doc = Nokogiri::XML(File.open(findbugs_report))
	src_dirs = doc.xpath('//SrcDir').map { |node| node.text }
	doc.xpath('//BugInstance').each do |node|
		errors_count += 1
		src  = node.xpath('./Class/SourceLine').first
		from_line = src['start']
		to_line = src['end']
		line = "#L#{from_line}"
		if to_line.to_i > from_line.to_i
			line << '-L' << to_line
		end
		msg  = node.xpath('./LongMessage').first.text
		file = src['sourcepath']
		src_dir = src_dirs.find { |dir| File.file?("#{dir}/#{file}") }
		src_dir = src_dir.sub(pwd, '')
		file = github.html_link("#{src_dir}/#{file}#{line}")
		fail("findbugs-maven-plugin error in #{file}:\n#{msg}")
	end
	print_errors_summary 'findbugs-maven-plugin', errors_count, 'https://github.com/php-coder/mystamps/wiki/findbugs'
end

# Handle `mvn robotframework:run` report
#
# Example:
# <suite source="/home/coder/mystamps/src/test/robotframework/category/access.robot" name="Access" id="s1-s1-s1">
#   <test name="Create category with name in English and Russian" id="s1-s1-s2-s1-t2">
#     <status critical="yes" endtime="20170301 20:27:07.476" starttime="20170301 20:27:06.810" status="FAIL">
#       The text of element 'id=page-header' should have been 'Space!', but it was 'Space'.
#     </status>
#   </test>
# </suite>
rf_report = 'target/robotframework-reports/output.xml'
unless File.file?(rf_report)
	warn("Couldn't find #{rf_report}. robotframework-maven-plugin result is unknown")
else
	errors_count = 0
	doc = Nokogiri::XML(File.open(rf_report))
	doc.xpath('//status[@critical="yes"][@status="FAIL"]').each do |node|
		errors_count += 1
		# find first parent's <suite> tag
		suite = node.parent
		while suite.name != 'suite'
			suite = suite.parent
		end
		msg  = node.text.sub(/\.$/, '')
		file = suite['source'].sub(pwd, '')
		file = github.html_link(file)
		testcase = node.parent['name']
		# TODO: try to findout the test case and use it for highlighting line numbers
		fail("robotframework-maven-plugin error in #{file}:\nTest case `#{testcase}` fails with message:\n#{msg}")
	end
	# TODO: add link to wiki page (#530)
	print_errors_summary 'robotframework-maven-plugin', errors_count
end
