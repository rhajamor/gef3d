README

This project is a template which defines a minimal setup for running a build. You need:
 
* One or more maps/*.map (unless building from local sources)
* boilerplate build.xml
* build.properties

For testing, use:

* testing.properties (defines which tests to run and sets extra properties)
* testExtra.xml (additional steps to do for testing)

For publishing your build once done, use:

* promote.properties (one or more)
* boilerplate promote.xml

For extra build or packaging steps, use:

* buildExtra.xml (overrides to generic build.xml)

You can also use PDE hooks such as:

allElements.xml
customTargets.xml
customAssembly.xml
myproduct.product (not yet supported)
pack.properties (to disable packing for some plugins)

You may find that copying from a working example is more meaningful than using the sample "foo" project here. See links below.

---------------------------------------------------------------------------

Gotchas:

You must ensure your plugins set Bundle-RequireExecutionEnvironment (BREE) correctly, since the build will NOT.

---------------------------------------------------------------------------

Latest documentation:

http://wiki.eclipse.org/Common_Build_Infrastructure/Getting_Started/Build_In_Eclipse
http://wiki.eclipse.org/Common_Build_Infrastructure/Getting_Started/FAQ
http://wiki.eclipse.org/Category:Athena_Common_Build
http://wiki.eclipse.org/Common_Build_Infrastructure/Defining_Binary_Dependencies

---------------------------------------------------------------------------

Sample projects from which to copy:

CVS:

cvs -d :pserver:anonymous@dev.eclipse.org:/cvsroot/technology -q co org.eclipse.dash/athena/org.eclipse.dash.commonbuilder/*.releng

SVN:

svn -q co http://dev.eclipse.org/svnroot/technology/org.eclipse.linuxtools/releng/trunk/org.eclipse.linuxtools.releng/
svn -q co http://anonsvn.jboss.org/repos/jbosstools/trunk/jmx/releng/
svn -q co http://anonsvn.jboss.org/repos/jbosstools/trunk/bpel/releng/
svn -q co http://anonsvn.jboss.org/repos/jbosstools/trunk/jbpm/releng/

Sample Hudson configuration scripts:

https://build.eclipse.org/hudson/view/Athena%20CBI/
