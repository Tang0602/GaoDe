#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试脚本：验证指令#19 - 查找并预订最贵的酒店
"""

import subprocess
import json
import sys
import os

def run_adb_command(command):
    """执行ADB命令并返回结果"""
    try:
        result = subprocess.run(command, shell=True, capture_output=True, text=True, encoding='utf-8')
        if result.returncode != 0:
            # 如果UTF-8失败，尝试GBK编码
            try:
                result = subprocess.run(command, shell=True, capture_output=True, text=True, encoding='gbk')
            except:
                pass
        return result.stdout.strip()
    except Exception as e:
        print(f"执行ADB命令时发生错误: {e}")
        return ""

def verify_hotel_booking():
    """验证酒店预订日志"""
    print("=" * 50)
    print("测试指令#19: 查找并预订最贵的酒店")
    print("=" * 50)
    
    # 获取应用的私有文件
    adb_command = 'adb shell "run-as com.example.GaoDe cat files/19_hotel_booking_history.json"'
    result = run_adb_command(adb_command)
    
    if not result:
        print("❌ 错误: 无法读取日志文件或文件为空")
        return False
    
    try:
        # 解析JSON数据
        log_entries = json.loads(result)
        print(f"📝 找到 {len(log_entries)} 条日志记录")
        
        # 验证是否有酒店预订相关的记录 (条件日志: 仅当预订凯悦酒店时记录)
        booking_found = False
        for entry in log_entries:
            action = entry.get('action', '')
            if "凯悦" in action or ("最贵" in action and "酒店" in action):
                print(f"✅ 找到酒店预订记录:")
                print(f"   时间: {entry.get('timestamp', 'N/A')}")
                print(f"   动作: {action}")
                print(f"   页面: {entry.get('page', 'N/A')}")
                booking_found = True
        
        if booking_found:
            print("🎉 指令#19 验证成功: 酒店预订功能正常工作")
            return True
        else:
            print("❌ 指令#19 验证失败: 未找到酒店预订相关记录")
            return False
            
    except json.JSONDecodeError as e:
        print(f"❌ JSON解析错误: {e}")
        return False
    except Exception as e:
        print(f"❌ 验证过程中发生错误: {e}")
        return False

if __name__ == "__main__":
    success = verify_hotel_booking()
    sys.exit(0 if success else 1)