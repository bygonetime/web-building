define([ 'cms/module', 'common/context' ], function(cmsModule) {
	return cmsModule.factory('cmsService', [ '$http', 'util', function($http, util) {
		return {
			/**
			 * 获取资源管理的根目录的数据结构
			 */
			getFileRoot : function() {
				return $http.get('fileUploadServer/root');
			},
			
			/**
			 * 创建一个目录
			 */
			createDir : function(dirName) {
				return $http({
					method : 'POST',
					url  : 'fileUploadServer/createDir',
					data : 'dirName=' + encodeURIComponent(dirName),
					headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8' }
				});
			},
			
			/**
			 * 为目录或文件改名
			 */
			reName : function(srcName, destName) {
				return $http({
					method : 'POST',
					url  : 'fileUploadServer/reName',
					data : 'srcName=' + encodeURIComponent(srcName) + '&destName=' + destName,
					headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8' }
				});
			},
			
			/**
			 * 删除目录或文件
			 */
			'delete' : function(filename) {
				return $http({
					method : 'POST',
					url  : 'fileUploadServer/delete',
					data : 'filename=' + encodeURIComponent(filename),
					headers : { 'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8' }
				});
			},
			
		};
	}]);
});