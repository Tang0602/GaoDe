import subprocess
import json
import os

def format_message_history():
    """将消息历史文件格式化"""
    try:
        # 从设备读取当前文件
        result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 'cat', 'files/1_message_history.json'],
                               stdout=subprocess.PIPE, stderr=subprocess.PIPE, encoding='utf-8', errors='ignore')
        
        if result.returncode == 0 and result.stdout:
            # 解析JSON
            data = json.loads(result.stdout)
            
            # 创建格式化的临时文件
            temp_file = os.path.join(os.path.dirname(__file__), 'temp_formatted.json')
            with open(temp_file, 'w', encoding='utf-8') as f:
                json.dump(data, f, ensure_ascii=False, indent=2)
            
            # 推送格式化文件到设备
            push_result = subprocess.run(['adb', 'push', temp_file, '/sdcard/temp_formatted.json'], 
                                       capture_output=True, text=True)
            
            if push_result.returncode == 0:
                # 替换原文件
                move_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 
                                            'cp', '/sdcard/temp_formatted.json', 'files/1_message_history.json'],
                                           capture_output=True, text=True)
                
                # 清理临时文件
                subprocess.run(['adb', 'shell', 'rm', '/sdcard/temp_formatted.json'], capture_output=True)
                os.remove(temp_file)
                
                if move_result.returncode == 0:
                    print("✅ JSON文件已格式化")
                    return True
                else:
                    print(f"格式化失败: {move_result.stderr}")
                    return False
            else:
                print(f"推送失败: {push_result.stderr}")
                return False
        else:
            print("无法读取文件")
            return False
            
    except Exception as e:
        print(f"格式化失败: {e}")
        return False

if __name__ == "__main__":
    format_message_history()