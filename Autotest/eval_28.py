import subprocess
import json
import os
from datetime import datetime

def update_comprehensive_test_history(action_type, test_results):
    """更新综合筛选最优餐厅历史记录"""
    try:
        timestamp = int(datetime.now().timestamp() * 1000)
        comprehensive_record = {
            "id": f"comprehensive_{timestamp}",
            "action": action_type,
            "timestamp": timestamp,
            "formattedTime": datetime.fromtimestamp(timestamp / 1000).strftime("%Y-%m-%d %H:%M:%S"),
            "testResults": test_results,
            "selectedRestaurant": "巴奴毛肚火锅",
            "criteria": ["距离", "价格", "评分", "用户评价"],
            "success": True
        }
        
        history_file = os.path.join(os.path.dirname(__file__), '28_综合筛选餐厅历史.json')
        history_records = []
        
        if os.path.exists(history_file):
            try:
                with open(history_file, 'r', encoding='utf-8') as f:
                    history_records = json.load(f)
            except json.JSONDecodeError:
                history_records = []
        
        history_records.append(comprehensive_record)
        
        with open(history_file, 'w', encoding='utf-8') as f:
            json.dump(history_records, f, ensure_ascii=False, indent=2)
        
        print(f"✅ 综合筛选餐厅历史记录已更新: {action_type}")
        return True
        
    except Exception as e:
        print(f"更新综合筛选餐厅历史失败: {e}")
        return False

def ComprehensiveRestaurantSelectionCheck():
    """检测是否成功综合筛选最优餐厅"""
    try:
        result = subprocess.run(['adb', 'exec-out', 'uiautomator', 'dump', '/dev/stdout'], 
                              capture_output=True, text=True)
        
        if result.returncode == 0:
            ui_dump = result.stdout
            
            # 检查是否显示了多个餐厅的对比信息
            restaurant_comparison = [
                '巴奴毛肚火锅', '木屋烧烤', '麦当劳',
                '4.5分', '4.7分', '4.3分',
                '¥148/人', '¥85/人', '¥35/人',
                '4.2公里', '驾车 17分钟'
            ]
            
            found_indicators = [indicator for indicator in restaurant_comparison if indicator in ui_dump]
            
            if len(found_indicators) >= 6:  # 至少要有多个餐厅的信息
                print(f"✓ 在UI中找到餐厅对比元素: {', '.join(found_indicators)}")
                
                # 分析最优选择
                test_results = {
                    "巴奴火锅": {"评分": 4.5, "价格": 148, "距离": "4.2公里"},
                    "木屋烧烤": {"评分": 4.7, "价格": 85, "距离": "较近"},
                    "麦当劳": {"评分": 4.3, "价格": 35, "距离": "较近"}
                }
                
                update_comprehensive_test_history("综合筛选最优餐厅", test_results)
                return True
            else:
                print("✗ 未在UI中找到足够的餐厅对比信息")
                return False
        else:
            print(f"UI检测失败: {result.stderr}")
            return False
            
    except Exception as e:
        print(f"综合筛选餐厅检测失败: {e}")
        return False

if __name__ == "__main__":
    print("开始检测：综合筛选最优餐厅")
    result = ComprehensiveRestaurantSelectionCheck()
    print(f"检测结果: {'通过' if result else '失败'}")