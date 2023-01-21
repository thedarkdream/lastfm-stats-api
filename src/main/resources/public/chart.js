var createDataItem = function(o) {
    return {
        label: o.artist,
        data: o.points,
        borderWidth: 3,
        tension: 0.3
    }
};

export default {
  data() {
    return {
      chart: null
    }
  },
//    props: {
//        labels: Array,
//        data: Array,
//        chartId: {
//            type: String,
//            default: "myChart"
//        }
//    },
    props: {
        timeline: Object,
        chartId: {
            type: String,
            default: "myChart"
        }
    },
    template: `
        <canvas :id="chartId"></canvas>
    `,
    methods: {
        render() {
            const ctx = document.getElementById(this.chartId);
            this.chart = new Chart(ctx, {
                type: 'line',
                data: {
                  labels: this.timeline.labels,
                  datasets: this.timeline.points.map((o) => createDataItem(o))
                },
                options: {
                  scales: {
                    y: {
                      beginAtZero: true
                    }
                  }
                }
            });
        }
    },
    mounted: function() {
        this.render();
    },
    watch: {
        timeline: function(oldVal, newVal) {
        this.chart.destroy();
         this.render();
        }
    }
}