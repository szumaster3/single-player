A fork of <a href="https://gitlab.com/2009scape/2009scape">2009Scape</a> with AGPL-3.0 licensing.
<ul>
<ol><a href="#prerequisites">Prerequisites</a></ol>
<ol><a href="#fork--clone">Fork & Clone</a></ol>
<ol><a href="#import-project-in-intellij">Import Project</a></ol>
<ol><a href="#setup-git--ssh">Git & SSH</a></ol>
<ol><a href="#build-project">Build</a></ol>
<ol><a href="#run-project">Run</a></ol>
<ol><a href="#contributing">Contributing</a></ol>
<ol><a href="#license">License</a></ol>
</ul>

<h1>Prerequisites</h1>
<p>Before setting up the project, ensure the following:</p>

<ul>
  <li><strong>Java 11</strong> – Download from <a href="https://www.oracle.com/java/technologies/javase-jdk11-downloads.html" target="_blank">Oracle</a> or <a href="https://adoptium.net/temurin/releases/?version=11" target="_blank">Adoptium</a></li>
  <li><strong>IntelliJ IDEA</strong> – Download from <a href="https://www.jetbrains.com/idea/download/" target="_blank">JetBrains</a></li>
</ul>

<blockquote>Windows users: Enable <strong>Developer Mode</strong> before proceeding.</blockquote>

<h1>Fork &amp; Clone</h1>

<ol>
  <li>Fork the repository on GitLab.</li>
  <li>Clone your fork:</li>
</ol>

<pre><code>git clone &lt;your-fork-ssh-or-https-url&gt;</code></pre>

<ol start="3">
  <li>Navigate into the folder:</li>
</ol>

<pre><code>cd &lt;your-project-folder&gt;</code></pre>

<h1>Import Project in IntelliJ</h1>

<ol>
  <li>Open IntelliJ IDEA.</li>
  <li>Select <code>File &gt; Open...</code> and choose the project root.</li>
  <li>IntelliJ should detect <code>pom.xml</code> and import the Maven project automatically.</li>
  <li>Set Project SDK to <strong>Java 11</strong> or higher.</li>
</ol>

<h1>Setup Git &amp; SSH</h1>

<p>Generate SSH key if you don't have one:</p>

<pre><code>ssh-keygen -t ed25519 -C "&lt;example@example.eu&gt;"</code></pre>

<ul>
  <li>Add your public key to GitLab.</li>
  <li>Configure Git:</li>
</ul>

<pre><code>git config --global user.name "example"
t config --global user.email "example@example.eu"</code></pre>

<h1>Build Project</h1>

<p>Run from the project root:</p>

<pre><code>mvn clean install</code></pre>

<p>This compiles and packages all files.</p>

<h1>Run Project</h1>

<pre><code>mvn exec:java -f pom.xml</code></pre>

<blockquote>Tip: Run via IntelliJ by right-clicking <code>pom.xml</code> &gt; <code>Run 'exec:java'</code>.</blockquote>

<h1>Contributing</h1>

<ol>
  <li>Fork the repository.</li>
  <li>Create a feature branch:</li>
</ol>

<pre><code>git checkout -b feature/my-feature</code></pre>

<ol start="3">
  <li>Commit changes:</li>
</ol>

<pre><code>git commit -am "Changes"</code></pre>

<ol start="4">
  <li>Push and open a merge request.</li>
</ol>

<h1>Troubleshooting</h1>

<ul>
  <li><strong>Java version mismatch</strong>: <code>java -version</code> should be 11+.</li>
  <li><strong>Maven issues</strong>: Check with <code>mvn -version</code>.</li>
  <li><strong>IDE errors</strong>: Reimport Maven project or invalidate caches (<code>File &gt; Invalidate Caches / Restart</code>).</li>
  <li><strong>SSH issues</strong>: Ensure public key is added to GitLab.</li>
</ul>

<h1>License</h1>

<p>This project is licensed under <strong>AGPL-3.0</strong>. See 
<a href="./LICENSE" target="_blank">LICENSE</a> or 
<a href="https://www.gnu.org/licenses/agpl-3.0.html" target="_blank">gnu.org</a>.</p>
