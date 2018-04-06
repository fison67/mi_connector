/**
 *  Test
 *
 */
metadata {
	definition (name: "test2", namespace: "test", author: "test") {
	}

	tiles(scale:2) {
        
        htmlTile(name:"testTile", action:"getGraphHTML", 
        	whitelist:["code.jquery.com", "ajax.googleapis.com", 
       ], width:6, height:8){}
        
        main (["testTile"])
        details (["testTile"])
	}
    
    
}

mappings {
      path("/getGraphHTML") {action: [ GET: "getGraphHTML" ]}
}

def getGraphHTML() {
	def html = """
		<!DOCTYPE html>
			<html>
				<head>
                	
                      <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
					<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.5.0/Chart.min.js"></script>
					<script type="text/javascript">
                    	\$( document ).ready(function() {
                            // Bar chart
                            new Chart(document.getElementById("bar-chart"), {
                                type: 'bar',
                                data: {
                                  labels: ["Africa", "Asia", "Europe", "Latin America", "North America"],
                                  datasets: [
                                    {
                                      label: "Population (millions)",
                                      backgroundColor: ["#3e95cd", "#8e5ea2","#3cba9f","#e8c3b9","#c45850"],
                                      data: [2478,5267,734,784,433]
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
                        });
						
					</script>
				</head>
				<body>
					<canvas id="bar-chart" width="800" height="450"></canvas>
				</body>
			</html>
		"""
	render contentType: "text/html", data: html, status: 200
}
