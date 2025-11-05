import subprocess
import json
import os
from datetime import datetime

def update_scenic_search_history(search_query):
    """更新景点搜索历史记录"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        search_record = {
            "id": f"scenic_search_{timestamp}",
            "query": search_query,
            "category": "景点",
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "resultCount": 3,
            "success": True
        }
        
        history_file = os.path.join(os.path.dirname(__file__), '11_景点搜索历史.json')
        history_records = []
        
        if os.path.exists(history_file):
            try:
                with open(history_file, 'r', encoding='utf-8') as f:
                    history_records = json.load(f)
            except json.JSONDecodeError:
                history_records = []
        
        history_records.append(search_record)
        
        with open(history_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 景点搜索历史记录已更新: {search_query}")
        return True
        
    except Exception as e:
        print(f"更新景点搜索历史失败: {e}")
        return False

def ScenicSpotCheck():
    """检测是否成功查看景点列表"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True)
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            scenic_indicators = [
                '武汉欢乐谷', '东湖风景区', '黄鹤楼', '4A级景区', '3A级景区',
                '门票详询', '免费开放', '过山车', '摩天轮', '游船步道'
            ]
            
            found_indicators = [indicator for indicator in scenic_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"✓ 在UI中找到景点列表元素: {', '.join(found_indicators)}")
                update_scenic_search_history("景点")
                return True
            else:
                print("✗ 未在UI中找到景点列表元素")
                return False
        else:
            print(f"UI检测失败: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"景点列表检测失败: {e}")
        return False

if __name__ == "__main__":
    print("开始检测：查看景点列表")
    result = ScenicSpotCheck()
    print(f"检测结果: {'通过' if result else '失败'}")