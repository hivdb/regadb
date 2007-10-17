;RegaDB Installation Script
;Written by Pieter Libin
;pieter.libin@uz.kuleuven.ac.be 

;--------------------------------
  !include "MUI.nsh"
  !include "LogicLib.nsh"
  !include "file-utils.nsh"
  !include "WriteEnvStr.nsh"
;--------------------------------

;General

!define FILE_SOURCE "$FILE_SOURCE$"

  ;Name and file
  Name "RegaDB"
  OutFile "regadb_install.exe"

  ;Default installation folder
  InstallDir "$PROGRAMFILES\Rega Institute\RegaDB"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\RegaDB" ""

Function .onInit
  !insertmacro MUI_INSTALLOPTIONS_EXTRACT "db_page.ini"
  !insertmacro MUI_INSTALLOPTIONS_EXTRACT "db_specific_page.ini"
  !insertmacro MUI_INSTALLOPTIONS_EXTRACT "proxy_page.ini"
FunctionEnd

;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
;Pages

  !define MUI_LICENSEPAGE_CHECKBOX	
  !insertmacro MUI_PAGE_LICENSE "gpl-2.0.txt"
  !define MUI_PAGE_CUSTOMFUNCTION_LEAVE PAGE_COMPONENTS_LEAVE
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  Page custom ShowDBConfPage LeaveDBConfPage 
  Page custom ShowDBSpecificConfPage LeaveDBSpecificConfPage 
  Page custom ShowProxyConfPage LeaveProxyConfPage 
  !insertmacro MUI_PAGE_INSTFILES
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  
  !insertmacro MUI_LANGUAGE "English"
  
  ; Brand text 
  BrandingText "RegaDB Installer"

;Variables
Var db_dialect
Var db_url
Var db_user
Var db_password
Var proxy_url_a
Var proxy_port_a
Var proxy_url_b
Var proxy_port_b

;Installer Sections
Section "RegaDB Core" RegaDBCore 
  ;Make section mandatory  
  SectionIn RO
  SetOutPath "$INSTDIR\conf"
  File /r "${FILE_SOURCE}\conf\"
  SetOutPath "$INSTDIR\regadb-install"
  File /r "${FILE_SOURCE}\regadb-install\*"
  SetOutPath "$INSTDIR\jre"
  File /r "${FILE_SOURCE}\jre\*"

  ${WriteToFile} "install_dir $INSTDIR$\n" "$INSTDIR\conf\template.txt"
  ${WriteToFile} "db_dialect $db_dialect$\n" "$INSTDIR\conf\template.txt"
  ${WriteToFile} "db_url $db_url$\n" "$INSTDIR\conf\template.txt"
  ${WriteToFile} "db_user $db_user$\n" "$INSTDIR\conf\template.txt"
  ${WriteToFile} "db_password $db_password$\n" "$INSTDIR\conf\template.txt"
  ${WriteToFile} "proxy_url_a $proxy_url_a$\n" "$INSTDIR\conf\template.txt"
  ${WriteToFile} "proxy_port_a $proxy_port_a$\n" "$INSTDIR\conf\template.txt"
  ${WriteToFile} "proxy_url_b $proxy_url_b$\n" "$INSTDIR\conf\template.txt"
  ${WriteToFile} "proxy_port_b $proxy_port_b$\n" "$INSTDIR\conf\template.txt"

  StrCpy $0 '"$INSTDIR\jre\bin\java" -jar "$INSTDIR\regadb-install\regadb-install-regadb-conf.jar" "$INSTDIR\conf\template.txt"'
  SetOutPath $EXEDIR
  ExecWait $0

  RMDIR /R "$INSTDIR\jre"
  RMDIR /R "$INSTDIR\regadb-install"
  Delete "$INSTDIR\conf\template.txt"
  CreateDirectory "$INSTDIR\queryResult"

  Push "REGADB_CONF_DIR"
  Push "$INSTDIR\conf"
  Call WriteEnvStr
SectionEnd

Section "RegaDB/Java/Tomcat" JavaTomcat
  SetOutPath "$INSTDIR\tomcat"
  File /r "${FILE_SOURCE}\tomcat\*"
  SetOutPath "$INSTDIR\jre"
  File /r "${FILE_SOURCE}\jre\*"
  SetOutPath "$INSTDIR\hsqldb"
  File /r "${FILE_SOURCE}\hsqldb\*"

  Push "REGADB_JRE_HOME"
  Push "$INSTDIR\jre"
  Call WriteEnvStr
 
  ;Store installation folder
  WriteRegStr HKCU "Software\Modern UI Test" "" $INSTDIR
  
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
SectionEnd

Section "RegaDB Browser" RegaDBBrowser
  SetOutPath "$INSTDIR\regadb-browser"
  File /r "${FILE_SOURCE}\regadb-browser\*"
  
  ;create desktop shortcut
  CreateShortCut "$DESKTOP\RegaDB.lnk" "$INSTDIR\jre\bin\javaw.exe" "-jar $\"$INSTDIR\regadb-browser\regadb-browser-run.jar$\"" "$INSTDIR\regadb-browser\regadb-browser.ico" 0
 
  ;create start-menu items
  CreateDirectory "$SMPROGRAMS\Rega Institute\"
  CreateDirectory "$SMPROGRAMS\Rega Institute\RegaDB\"
  CreateShortCut "$SMPROGRAMS\Rega Institute\RegaDB\RegaDB.lnk" "$INSTDIR\jre\bin\javaw.exe" "-jar $\"$INSTDIR\regadb-browser\regadb-browser-run.jar$\"" "$INSTDIR\regadb-browser\regadb-browser.ico" 0
SectionEnd

  ;Language strings
  LangString DESC_JavaTomcat ${LANG_ENGLISH} "Installs a bundle containing the Java Runtime, Tomcat and RegaDB."
  LangString DESC_RegaDBCore ${LANG_ENGLISH} "RegaDB Core software."
  LangString DESC_RegaDBBrowser ${LANG_ENGLISH} "RegaDB Browser, allows you to start RegaDB with a click on the Desktop."
  LangString TEXT_DB_PAGE_TITLE ${LANG_ENGLISH} "Database configuration page"
  LangString TEXT_DB_PAGE_SUB_TITLE ${LANG_ENGLISH} "If you're unsure, pick the default."
  LangString TEXT_DB_DETAILS_PAGE_TITLE ${LANG_ENGLISH} "Database specific configuration page"
  LangString TEXT_DB_DETAILS_PAGE_SUB_TITLE ${LANG_ENGLISH} "Configuration of your database dialect."
  LangString TEXT_PROXY_PAGE_TITLE ${LANG_ENGLISH} "Proxy configuration page"
  LangString TEXT_PROXY_PAGE_SUB_TITLE ${LANG_ENGLISH} "Configure your proxy settings to access external services,$\nit is possible to configure a second proxy if you use RegaDB on different locations."

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${JavaTomcat} $(DESC_JavaTomcat)
    !insertmacro MUI_DESCRIPTION_TEXT ${RegaDBCore} $(DESC_RegaDBCore)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

Section "Uninstall"
  ;ADD YOUR OWN FILES HERE...

  Delete "$INSTDIR\Uninstall.exe"

  RMDir "$INSTDIR"

  DeleteRegKey /ifempty HKCU "Software\RegaDB"
SectionEnd

Function PAGE_COMPONENTS_LEAVE 
  ;check if the section is selected
  SectionGetFlags 1 $1
  ${If} $1 == 0 
  MessageBox MB_OK "You did choose not to install the RegaDB/Java/Tomcat bundle.$\nThis means that you will need to install the regadb.war manually in your Tomcat installation.$\nPlease refer to the installation manual for further instructions."
  ${EndIf}
FunctionEnd

Function ShowDBConfPage
  !insertmacro MUI_HEADER_TEXT "$(TEXT_DB_PAGE_TITLE)" "$(TEXT_DB_PAGE_SUB_TITLE)"
  !insertmacro MUI_INSTALLOPTIONS_DISPLAY "db_page.ini"
FunctionEnd

Function LeaveDBConfPage
  !insertmacro MUI_INSTALLOPTIONS_READ $1 "db_page.ini" "Settings" "State"
  !insertmacro MUI_INSTALLOPTIONS_READ $2 "db_page.ini" "Field 2" "State"
  ${If} $1 == 2
    Abort
  ${EndIf}
  StrCpy $db_dialect $2 	
FunctionEnd

Function ShowDBSpecificConfPage
  ${If} $db_dialect != "Default"
    !insertmacro MUI_HEADER_TEXT "$(TEXT_DB_DETAILS_PAGE_TITLE)" "$(TEXT_DB_DETAILS_PAGE_SUB_TITLE)"
    !insertmacro MUI_INSTALLOPTIONS_DISPLAY "db_specific_page.ini"
  ${EndIf}
FunctionEnd

Function LeaveDBSpecificConfPage
  !insertmacro MUI_INSTALLOPTIONS_READ $2 "db_specific_page.ini" "Field 2" "State"
  StrCpy $db_url $2 	
  !insertmacro MUI_INSTALLOPTIONS_READ $2 "db_specific_page.ini" "Field 4" "State"
  StrCpy $db_user $2 	
  !insertmacro MUI_INSTALLOPTIONS_READ $2 "db_specific_page.ini" "Field 6" "State"
  StrCpy $db_password $2 	
FunctionEnd

Function ShowProxyConfPage
  !insertmacro MUI_HEADER_TEXT "$(TEXT_PROXY_PAGE_TITLE)" "$(TEXT_PROXY_PAGE_SUB_TITLE)"
  !insertmacro MUI_INSTALLOPTIONS_DISPLAY "proxy_page.ini"
FunctionEnd

Function LeaveProxyConfPage
  !insertmacro MUI_INSTALLOPTIONS_READ $2 "proxy_page.ini" "Field 2" "State"
  StrCpy $proxy_url_a $2 	
  !insertmacro MUI_INSTALLOPTIONS_READ $2 "proxy_page.ini" "Field 4" "State"
  StrCpy $proxy_port_a $2 	
  !insertmacro MUI_INSTALLOPTIONS_READ $2 "proxy_page.ini" "Field 6" "State"
  StrCpy $proxy_url_b $2 	
  !insertmacro MUI_INSTALLOPTIONS_READ $2 "proxy_page.ini" "Field 8" "State"
  StrCpy $proxy_port_b $2 	
FunctionEnd

Function EnableComponent
  ;ini_file
  Pop $2
  ;Field
  Pop $3
  !insertmacro MUI_INSTALLOPTIONS_READ $1 $2 $3 "HWND"
  EnableWindow $1 1
  !insertmacro MUI_INSTALLOPTIONS_READ $1 $2 $3 "HWND2"
  EnableWindow $1 1
FunctionEnd

Function DisableComponent
  ;ini_file
  Pop $2
  ;Field
  Pop $3
  !insertmacro MUI_INSTALLOPTIONS_READ $1 $2 $3 "HWND"
  EnableWindow $1 0
  !insertmacro MUI_INSTALLOPTIONS_READ $1 $2 $3 "HWND2"
  EnableWindow $1 0
FunctionEnd
