import React, { useState } from 'react';
import { JAVA_SOURCES } from '../data/javaSources';
import { JavaFileSource } from '../types';
import JSZip from 'jszip';
import {
  X,
  Folder,
  FileCode,
  Download,
  Copy,
  Check,
  Package,
  Terminal,
  ExternalLink,
  Code2
} from 'lucide-react';

interface JavaCodeHubModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const JavaCodeHubModal: React.FC<JavaCodeHubModalProps> = ({
  isOpen,
  onClose
}) => {
  const [selectedFile, setSelectedFile] = useState<JavaFileSource>(JAVA_SOURCES[0]);
  const [copied, setCopied] = useState(false);
  const [downloadingZip, setDownloadingZip] = useState(false);

  if (!isOpen) return null;

  const handleCopyCode = () => {
    navigator.clipboard.writeText(selectedFile.code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleDownloadSingle = () => {
    const blob = new Blob([selectedFile.code], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = selectedFile.name;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  const handleDownloadZip = async () => {
    setDownloadingZip(true);
    try {
      const zip = new JSZip();

      // Add schema.sql
      const schemaFile = JAVA_SOURCES.find((s) => s.path === 'schema.sql');
      if (schemaFile) {
        zip.file('schema.sql', schemaFile.code);
      }

      // Add README.md
      const readme = `# VentureLens — Desktop Operating System & Decision Intelligence Suite

Production-ready Java SE (JDK 17+/21) desktop application built with strictly standard JDK libraries and pure Java Swing (\`javax.swing\`) & AWT (\`java.awt\`).

## Technical Specifications & Architectural Mandates:
1. STRICTLY STANDARD JDK ONLY: Uses standard Java SE (JDK 17+ or 21).
2. UI FRAMEWORK: Strictly pure Java Swing (\`javax.swing\`) and AWT (\`java.awt\`, \`java.awt.geom\`, \`java.awt.event\`). No third-party Look-and-Feels.
3. NO EXTERNAL UTILITIES: Zero Maven/Gradle third-party dependencies (no Gson, Jackson, Commons, Lombok, JFreeChart, OpenNLP).
4. DATABASE: Strictly MySQL official JDBC (\`java.sql.*\`). All connections run inside \`try-with-resources\`. UI operations use \`javax.swing.SwingWorker\` to keep EDT responsive.
5. SINGLE-WINDOW SHELL: \`MainFrame\` with \`CardLayout\` manages all transitions without popups.

## Project Structure:
- \`schema.sql\` : Production MySQL 8.0+ DDL with indexes and foreign keys.
- \`src/com/venturelens/Main.java\` : Application entry point with anti-aliasing.
- \`src/com/venturelens/model/\` : User, UserSession, DecisionTier, VentureIdea, CapTableState, BurnEntry, FinancialLedger.
- \`src/com/venturelens/utils/\` : ThemeColors, SecurityUtil (SHA-256), UIHelper.
- \`src/com/venturelens/analysis/\` : NLPValidatorEngine (rule-based NLP regex with lookbehind negation detection).
- \`src/com/venturelens/dao/\` : DatabaseManager, UserDAO, VentureDAO.
- \`src/com/venturelens/ui/components/\` : DonutChartPanel (Graphics2D fillArc), RunwayLineChartPanel (Graphics2D curve), DecisionBadgeRenderer.
- \`src/com/venturelens/ui/\` : MainFrame, LoginView, RegisterView, OverviewView, IdeaValidatorView, CapTableView, BurnWatchView, PitchCraftView, HistoryView, PitchDeckExporter.

## How to Compile & Run:

1. Import schema into MySQL:
   mysql -u root -p < schema.sql

2. Download MySQL Connector/J driver jar:
   Place \`mysql-connector-j-8.x.x.jar\` into a \`lib/\` folder.

3. Compile all Java sources:
   javac -d bin -cp "lib/*" $(find src -name "*.java")

4. Launch VentureLens:
   java -cp "bin:lib/*" com.venturelens.Main
`;
      zip.file('README.md', readme);

      // Add all source files
      JAVA_SOURCES.forEach((src) => {
        if (src.path !== 'schema.sql') {
          zip.file(src.path, src.code);
        }
      });

      const content = await zip.generateAsync({ type: 'blob' });
      const url = URL.createObjectURL(content);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'venturelens-java-desktop-suite.zip';
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (err) {
      console.error('Failed to generate zip', err);
    } finally {
      setDownloadingZip(false);
    }
  };

  const categories = ['SQL', 'Entry', 'Model', 'Analysis', 'DAO', 'UI Component', 'UI View', 'Utils'];

  return (
    <div
      id="modal-java-code-hub"
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 backdrop-blur-sm p-4 animate-in fade-in duration-200"
    >
      <div className="bg-[#162235] border border-[#273852] rounded-2xl w-full max-w-6xl h-[88vh] flex flex-col shadow-2xl overflow-hidden">
        {/* Modal Header */}
        <div className="px-6 py-4 border-b border-[#273852] flex items-center justify-between bg-[#111A29]">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg bg-[#10B981]/20 border border-[#10B981]/30 flex items-center justify-center text-[#10B981]">
              <Code2 className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-base font-bold text-[#F1F5F9] flex items-center gap-2">
                <span>Java Desktop Architecture & Source Code Hub</span>
                <span className="text-[10px] px-2 py-0.5 rounded bg-[#10B981]/20 text-[#10B981] font-mono">
                  JDK 21 &bull; Pure Swing &bull; No 3P Libs
                </span>
              </h3>
              <p className="text-xs text-[#94A3B8]">
                Production codebase adhering strictly to standard JDK and MySQL JDBC specifications.
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <button
              id="btn-download-all-zip"
              onClick={handleDownloadZip}
              disabled={downloadingZip}
              className="flex items-center gap-2 px-3.5 py-1.5 rounded-lg text-xs font-bold text-white bg-[#10B981] hover:bg-[#059669] transition-all cursor-pointer shadow-xs disabled:opacity-50"
            >
              <Download className="w-4 h-4" />
              <span>{downloadingZip ? 'Packaging ZIP...' : 'Download Project (.zip)'}</span>
            </button>

            <button
              onClick={onClose}
              className="p-1.5 rounded-lg text-[#94A3B8] hover:text-[#F1F5F9] hover:bg-[#1E2C44] transition-colors cursor-pointer"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Modal Body: Sidebar File Explorer + Code Viewer */}
        <div className="flex-1 flex overflow-hidden">
          {/* File Tree Sidebar */}
          <div className="w-72 bg-[#0E1828] border-r border-[#273852] flex flex-col shrink-0 overflow-y-auto p-3">
            <div className="text-[11px] font-bold text-[#64748B] uppercase px-2 mb-2 tracking-wider">
              Project Structure ({JAVA_SOURCES.length} Files)
            </div>

            <div className="space-y-3">
              {categories.map((cat) => {
                const files = JAVA_SOURCES.filter((f) => f.category === cat);
                if (files.length === 0) return null;
                return (
                  <div key={cat} className="space-y-1">
                    <div className="flex items-center gap-1.5 px-2 text-[11px] font-semibold text-[#94A3B8]">
                      <Folder className="w-3.5 h-3.5 text-[#10B981]" />
                      <span>{cat}</span>
                    </div>
                    {files.map((file) => {
                      const isSelected = selectedFile.path === file.path;
                      return (
                        <button
                          key={file.path}
                          onClick={() => setSelectedFile(file)}
                          className={`w-full flex items-center gap-2 px-3 py-1.5 rounded-md text-xs font-mono text-left transition-colors cursor-pointer ${
                            isSelected
                              ? 'bg-[#10B981]/20 text-[#10B981] font-bold border border-[#10B981]/30'
                              : 'text-[#CBD5E1] hover:bg-[#162235]'
                          }`}
                        >
                          <FileCode className="w-3.5 h-3.5 shrink-0" />
                          <span className="truncate">{file.name}</span>
                        </button>
                      );
                    })}
                  </div>
                );
              })}
            </div>
          </div>

          {/* Code Viewer Panel */}
          <div className="flex-1 flex flex-col bg-[#0B1320] overflow-hidden">
            {/* File Info Bar */}
            <div className="px-5 py-3 bg-[#111A29] border-b border-[#273852] flex items-center justify-between">
              <div>
                <div className="flex items-center gap-2">
                  <span className="text-xs font-mono font-bold text-[#F1F5F9]">
                    {selectedFile.path}
                  </span>
                  <span className="text-[10px] px-2 py-0.5 rounded bg-[#1F2E47] text-[#94A3B8]">
                    {selectedFile.pkg}
                  </span>
                </div>
                <p className="text-[11px] text-[#94A3B8] mt-0.5">
                  {selectedFile.description}
                </p>
              </div>

              <div className="flex items-center gap-2">
                <button
                  id="btn-copy-java-file"
                  onClick={handleCopyCode}
                  className="flex items-center gap-1.5 px-2.5 py-1 rounded text-xs font-medium text-[#CBD5E1] bg-[#162235] hover:bg-[#1F2E47] border border-[#273852] transition-colors cursor-pointer"
                >
                  {copied ? (
                    <>
                      <Check className="w-3.5 h-3.5 text-[#10B981]" />
                      <span className="text-[#10B981]">Copied</span>
                    </>
                  ) : (
                    <>
                      <Copy className="w-3.5 h-3.5" />
                      <span>Copy</span>
                    </>
                  )}
                </button>

                <button
                  id="btn-download-java-file"
                  onClick={handleDownloadSingle}
                  className="flex items-center gap-1.5 px-2.5 py-1 rounded text-xs font-medium text-[#CBD5E1] bg-[#162235] hover:bg-[#1F2E47] border border-[#273852] transition-colors cursor-pointer"
                >
                  <Download className="w-3.5 h-3.5" />
                  <span>Download</span>
                </button>
              </div>
            </div>

            {/* Code Body with Line Numbers */}
            <div className="flex-1 overflow-auto p-4 font-mono text-xs text-[#E2E8F0] leading-relaxed selection:bg-[#10B981]/30">
              <pre className="whitespace-pre">
                {selectedFile.code.split('\n').map((line, i) => (
                  <div key={i} className="table-row">
                    <span className="table-cell pr-4 text-[#475569] select-none text-right w-10">
                      {i + 1}
                    </span>
                    <span className="table-cell">{line}</span>
                  </div>
                ))}
              </pre>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
