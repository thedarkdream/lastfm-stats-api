var createDataItem = function(o) {
    return {
        label: o.label,
        data: o.data,
        borderWidth: 3,
        tension: 0.3
    }
};

export default {
    props: {
        labels: Array,
        data: Array,
        chartId: {
            type: String,
            default: "myChart"
        }
    },
    template: `
        <canvas :id="chartId"></canvas>
    `,
    mounted: function() {
        const ctx = document.getElementById(this.chartId);

        new Chart(ctx, {
            type: 'line',
            data: {
              labels: this.labels,
              datasets: this.data.map((o) => createDataItem(o))
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
}