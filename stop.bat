@echo off
setlocal
title litemall 停止服务
echo 正在停止 litemall 服务（端口 8080 / 9527 / 6255）...

for %%p in (8080 9527 6255) do (
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%%p" ^| findstr "LISTENING"') do (
        echo   停止端口 %%p  (PID %%a)
        taskkill /f /pid %%a >nul 2>&1
    )
)

echo.
echo 完成。（MySQL 服务未停止，如需停止请手动执行 net stop MySQL80）
pause >nul
