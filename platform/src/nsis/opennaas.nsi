; Installer for OpenNaaS

;======================================================
; Includes

  !include MUI.nsh
  !include Sections.nsh
  !include ..\..\target\project.nsh

;======================================================
; Installer Information

  Name OpenNaaS

  RequestExecutionLevel admin
  SetCompressor /SOLID lzma
  XPStyle on
  CRCCheck on
  InstallDir "C:\OpenNaaS\"
  AutoCloseWindow false
  ShowInstDetails show
  Icon "opennaas-install-cloud.ico"

;======================================================
; Version Tab information for Setup.exe properties

  VIProductVersion 1.2.3.4
  VIAddVersionKey ProductName "OpenNaaS"
  VIAddVersionKey ProductVersion "${PROJECT_VERSION}"
  VIAddVersionKey CompanyName "${PROJECT_ORGANIZATION_NAME}"
  VIAddVersionKey FileVersion "${PROJECT_VERSION}"
  VIAddVersionKey FileDescription ""
  VIAddVersionKey LegalCopyright ""

;======================================================
; Variables


;======================================================
; Modern Interface Configuration

  !define MUI_HEADERIMAGE
  !define MUI_ABORTWARNING
  !define MUI_HEADERIMAGE_BITMAP_NOSTRETCH
  !define MUI_FINISHPAGE
  !define MUI_FINISHPAGE_TEXT "Thank you for installing OpenNaaS."
  !define MUI_ICON "opennaas-install-cloud.ico"
  !define MUI_HEADERIMAGE_BITMAP "opennaas-header.bmp"

;======================================================
; Modern Interface Pages

  !define MUI_DIRECTORYPAGE_VERIFYONLEAVE
  !insertmacro MUI_PAGE_LICENSE ..\..\..\LICENSE
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH

;======================================================
; Languages

  !insertmacro MUI_LANGUAGE "English"

;======================================================
; Installer Sections

Section "OpenNaaS"
    SetOutPath $INSTDIR
    SetOverwrite on
    
    ; hack to avoid long relative paths producing errors
	!cd "../../target/opennaas-${PROJECT_VERSION}/"
    File /r ".\"
	!cd "../../../../"

    writeUninstaller "$INSTDIR\uninstall.exe"
SectionEnd

Section "Start Menu Shortcuts"

  CreateDirectory "$SMPROGRAMS\OpenNaaS"
  CreateShortCut "$SMPROGRAMS\OpenNaaS\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\OpenNaaS\OpenNaaS-${PROJECT_VERSION}.lnk" "$INSTDIR\OpenNaaS-${PROJECT_VERSION}\bin\opennaas.bat" "" "$INSTDIR\OpenNaaS-${PROJECT_VERSION}\bin\opennaas.bat" 0
  
SectionEnd

; Installer functions
Function .onInstSuccess

FunctionEnd

Section "uninstall"
  RMDir /r "$SMPROGRAMS\OpenNaaS"
  Delete "$SMPROGRAMS\OpenNaaS"
  RMDir /r "$INSTDIR"
  Delete "$INSTDIR"
SectionEnd

Function .onInit
    InitPluginsDir
FunctionEnd