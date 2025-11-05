import subprocess
import json
import os
from datetime import datetime

def update_favorites_history(action_type):
    """更新收藏夹访问历史记录"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        favorites_record = {
            "id": f"fav_{timestamp}",
            "action": action_type,
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "page": "收藏夹页面",
            "success": True
        }
        
        history_file = os.path.join(os.path.dirname(__file__), '2_收藏夹历史.json')
        history_records = []
        
        if os.path.exists(history_file):
            try:
                with open(history_file, 'r', encoding='utf-8') as f:
                    history_records = json.load(f)
            except json.JSONDecodeError:
                history_records = []
        
        history_records.append(favorites_record)
        
        with open(history_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 收藏夹历史记录已更新: {action_type}")
        return True
        
    except Exception as e:
        print(f"更新收藏夹历史失败: {e}")
        return False

def FavoritesPageCheck():
    """检测是否成功查看收藏夹"""
    try:
        # 检查UI中是否存在收藏夹页面元素
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True)
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            # 检查收藏夹页面特有的UI元素
            favorites_indicators = [
                '我的收藏', '收藏夹', '南湖花溪公园', '黄鹤楼', '东湖生态旅游风景区',
                '家', '公司', '默认收藏夹'
            ]
            
            found_indicators = [indicator for indicator in favorites_indicators if indicator in ui_dump]
            
            if found_indicators:
                print(f"✓ 在UI中找到收藏夹页面元素: {', '.join(found_indicators)}")
                update_favorites_history("查看我的收藏夹")
                return True
            else:
                print("✗ 未在UI中找到收藏夹页面元素")
                return False
        else:
            print(f"UI检测失败: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"收藏夹页面检测失败: {e}")
        return False

if __name__ == "__main__":
    print("开始检测：查看我的收藏夹")
    result = FavoritesPageCheck()
    print(f"检测结果: {'通过' if result else '失败'}")