/**
 *  Test
 *
 */
metadata {
	definition (name: "test2", namespace: "test", author: "test") {
	}

	tiles(scale:2) {
        
        valueTile(
            "power",
            "device.power") {
                            state("power",
            label: '${currentValue}W',
            unit: "W",
            icon: "https://raw.githubusercontent.com/ahndee/Envoy-ST/master/devicetypes/aamann/enlighten-envoy-local.src/Solar.png",
            backgroundColors: [
                                    [value: 0, color: "#bc2323"],
                                    [value: 3000, color: "#1e9cbb"],
                                    [value: 6000, color: "#90d2a7"]
                                ])
                    }
        
        htmlTile(name:"testTile", action:"getGraphHTML", 
        	whitelist:["code.jquery.com", "ajax.googleapis.com", "https://raw.githubusercontent.com"
       ], width:6, height:8){}
        
        
        
        main (["power"])
        details (["power", "testTile"])
	}
    
    
}

mappings {
      path("/getGraphHTML") {action: [ GET: "getGraphHTML" ]}
}


def getGraphHTML() {
	state.testVal = 11
    
	def html = """
		<!DOCTYPE html>
			<html>
				<head>
                	
                      <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
					<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.5.0/Chart.min.js"></script>
					<script type="text/javascript">
                    
                    	\$( document ).ready(function() {
                        	//  데이터 가져오기
                            	getDataList();
                        });
                        
						// 차트 초기화
                        function initChart(dataList){
                        	// Bar chart
                            new Chart(document.getElementById("bar-chart"), {
                                type: 'bar',
                                data: {
                                  labels: ["Africa", "Asia", "Europe", "Latin America", "North America", "dthval"],
                                  datasets: [
                                    {
                                      label: "Population (millions)",
                                      backgroundColor: ["#3e95cd", "#8e5ea2","#3cba9f","#e8c3b9","#c45850", "#aaaaaa"],
                                      data: dataList
                                    }
                                  ]
                                },
                                options: {
                                  legend: { display: false },
                                  title: {
                                    display: true,
                                    text: 'Predicted world population (millions) in 2050'
                                  }
                                }
                        	});
                        }
                        
                        // 데이터 가져와서 차트 초기화 시키기
                        function getDataList(){
                        	// github에 올려져 있는 json 데이터 가져오기
							\$.get("https://raw.githubusercontent.com/fison67/mi_connector/master/dth/test/test.json", function(data, status){
                            //  데이터 수신 완료 후 json 파싱
                                var obj = \$.parseJSON( data );
                                var dataList = obj.data;  // 데이터리스트 
                                
                                // 데이터리스트에 state.testVal추가 시켜보기
                                dataList.push( ${state.testVal} )
                                // 차트 초기화
                                initChart(obj.data);
                            });
							
                        }
					</script>
				</head>
				<body>
					<canvas id="bar-chart" width="800" height="450"></canvas>
				</body>
			</html>
		"""
	render contentType: "text/html", data: html, status: 200
}
