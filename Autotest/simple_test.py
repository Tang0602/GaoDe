import subprocess
import json
import os

def simple_test():
    # 创建简单的JSON文件
    temp_file = os.path.join(os.path.dirname(__file__), 'simple_test.json')
    data = [{"test": "data", "id": 123}]
    
    with open(temp_file, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    
    print("Created temp file")
    
    # 推送到设备
    push_result = subprocess.run(['adb', 'push', temp_file, '/sdcard/simple_test.json'], 
                               capture_output=True, text=True)
    print(f"Push result: {push_result.returncode}")
    
    if push_result.returncode == 0:
        # 尝试复制到应用目录
        copy_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 
                                    'cp', '/sdcard/simple_test.json', 'files/simple_test.json'],
                                   capture_output=True, text=True)
        print(f"Copy result: {copy_result.returncode}")
        print(f"Copy stderr: {copy_result.stderr}")
        print(f"Copy stdout: {copy_result.stdout}")
        
        # 检查是否存在
        check_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 
                                     'ls', '-la', 'files/simple_test.json'],
                                    capture_output=True, text=True)
        print(f"File exists: {check_result.returncode == 0}")
        print(f"Check output: {check_result.stdout}")
        print(f"Check stderr: {check_result.stderr}")
        
        # 清理
        subprocess.run(['adb', 'shell', 'rm', '/sdcard/simple_test.json'], capture_output=True)
        os.remove(temp_file)

if __name__ == "__main__":
    simple_test()