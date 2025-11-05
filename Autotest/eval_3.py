import subprocess
import json
import os
from datetime import datetime

def update_food_search_history(search_query):
    """更新美食搜索历史记录"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        search_record = {
            "id": f"search_{timestamp}",
            "query": search_query,
            "category": "美食",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "resultCount": 4,
            "success": True
        }
        
        # 从设备读取现有历史记录
        device_file_path = 'files/3_美食搜索历史.json'
        history_records = []
        
        try:
            # 尝试从设备获取现有历史记录
            result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 'cat', device_file_path],
                                   stdout=subprocess.PIPE, stderr=subprocess.PIPE, encoding='utf-8', errors='ignore')
            if result.returncode == 0 and result.stdout and result.stdout.strip():
                try:
                    history_records = json.loads(result.stdout)
                except json.JSONDecodeError:
                    history_records = []
        except Exception:
            history_records = []
        
        # 添加新记录到历史
        history_records.append(search_record)
        
        # 创建临时文件并推送到设备
        temp_file = os.path.join(os.path.dirname(__file__), 'temp_food_search.json')
        with open(temp_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        # 推送文件到设备
        push_result = subprocess.run(['adb', 'push', temp_file, '/sdcard/temp_food_search.json'], 
                                   capture_output=True, text=True)
        
        if push_result.returncode == 0:
            # 将文件移动到应用内部存储
            move_result = subprocess.run(['adb', 'exec-out', 'run-as', 'com.example.GaoDe', 
                                        'cp', '/sdcard/temp_food_search.json', device_file_path],
                                       capture_output=True, text=True)
            
            # 清理临时文件
            subprocess.run(['adb', 'shell', 'rm', '/sdcard/temp_food_search.json'], capture_output=True)
            os.remove(temp_file)
            
            if move_result.returncode == 0:
                print(f"✅ 美食搜索历史记录已更新到设备内部存储: {search_query}")
                return True
            else:
                print(f"移动文件到设备内部存储失败: {move_result.stderr}")
                return False
        else:
            print(f"推送文件到设备失败: {push_result.stderr}")
            os.remove(temp_file)
            return False
        
    except Exception as e:
        print(f"更新美食搜索历史失败: {e}")
        return False

def FoodSearchCheck():
    """检测是否成功搜索美食"""
    try:
        # 检查UI中是否存在美食搜索结果页面元素
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True)
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            # 检查美食搜索结果页面特有的UI元素
            food_indicators = [
                '巴奴毛肚火锅', '木屋烧烤', '麦当劳', '老乡鸡',
                '美食', '火锅', '烧烤', '快餐', '人均',
                '4.5分', '4.7分', '4.3分', '4.6分'
            ]
            
            found_indicators = [indicator for indicator in food_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"✓ 在UI中找到美食搜索结果元素: {', '.join(found_indicators)}")
                update_food_search_history("美食")
                return True
            else:
                print("✗ 未在UI中找到美食搜索结果元素")
                return False
        else:
            print(f"UI检测失败: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"美食搜索检测失败: {e}")
        return False

if __name__ == "__main__":
    print("开始检测：搜索美食")
    result = FoodSearchCheck()
    print(f"检测结果: {'通过' if result else '失败'}")