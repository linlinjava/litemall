@echo off
setlocal enabledelayedexpansion
title litemall 一键启动
cd /d "%~dp0"

echo ================================================
echo            litemall 商城 一键启动
echo ================================================
echo.

REM ---- 1. 检查 MySQL 服务 ----
sc query MySQL80 | find "RUNNING" >nul 2>&1
if errorlevel 1 (
    echo [1/5] MySQL80 未运行，正在启动...
    net start MySQL80 >nul 2>&1
    if errorlevel 1 (
        echo       [错误] MySQL 启动失败，请手动启动后重试。
        goto :end
    )
)
echo [1/5] MySQL80 运行中 OK

REM ---- 2. 检查后端 jar，缺失则构建 ----
if not exist "litemall-all\target\litemall-all-0.1.0-exec.jar" (
    echo [2/5] 未找到后端 jar，开始构建（mvn clean package）...
    call mvn clean package -Dmaven.test.skip=true
    if errorlevel 1 (
        echo       [错误] 后端构建失败，请检查 Maven/网络。
        goto :end
    )
)
echo [2/5] 后端 jar 就绪 OK

REM ---- 3. 启动后端 8080 ----
netstat -ano | findstr ":8080" | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    echo [3/5] 启动后端 8080 ...
    start "litemall-backend-8080" cmd /k "cd /d %~dp0 && java -Dfile.encoding=UTF-8 -jar litemall-all\target\litemall-all-0.1.0-exec.jar"
) else (
    echo [3/5] 后端 8080 已在运行
)

REM ---- 4. 启动管理后台 9527 ----
netstat -ano | findstr ":9527" | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    echo [4/5] 启动管理后台 9527 ...
    start "litemall-admin-9527" cmd /k "cd /d %~dp0litemall-admin && set NODE_OPTIONS=--openssl-legacy-provider&& npm run dev"
) else (
    echo [4/5] 管理后台 9527 已在运行
)

REM ---- 5. 启动轻商城 6255 ----
netstat -ano | findstr ":6255" | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    echo [5/5] 启动轻商城 6255 ...
    start "litemall-vue-6255" cmd /k "cd /d %~dp0litemall-vue && set NODE_OPTIONS=--openssl-legacy-provider&& npm run dev"
) else (
    echo [5/5] 轻商城 6255 已在运行
)

echo.
echo 正在等待服务就绪（前端首次编译约需 30~60 秒，请勿关闭弹出的窗口）...
echo   8080=后端   9527=管理后台   6255=轻商城
echo.

call :waitport 8080 60
call :waitport 9527 90

echo.
echo 管理后台已就绪，正在打开浏览器 http://localhost:9527
start http://localhost:9527
echo 轻商城地址：http://localhost:6255

goto :end

:waitport
REM 参数：%1=端口  %2=最大等待秒数
set /a _n=0
:waitloop
timeout /t 3 /nobreak >nul
netstat -ano | findstr ":%1" | findstr "LISTENING" >nul 2>&1
if not errorlevel 1 goto :waitok
set /a _n+=3
if !_n! lss %2 goto :waitloop
echo   [提示] 端口 %1 在 %2 秒内未就绪，请稍后手动刷新页面。
goto :eof
:waitok
echo   [OK] 端口 %1 已就绪
goto :eof

:end
echo.
echo 完成。各服务运行在独立窗口中，关闭对应窗口即停止该服务。
pause >nul
